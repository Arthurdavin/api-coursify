package com.coursify.service.impl;

import com.coursify.client.BakongApiClient;
import com.coursify.client.BakongApiClient.BakongQrResult;
import com.coursify.config.BakongProperties;
import com.coursify.domain.*;
import com.coursify.domain.enums.EnrollmentStatus;
import com.coursify.domain.enums.PaymentStatus;
import com.coursify.dto.request.InitiatePaymentRequest;
import com.coursify.dto.response.PaymentResponse;
import com.coursify.exception.BadRequestException;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.exception.UnauthorizedException;
import com.coursify.repository.*;
import com.coursify.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository      paymentRepository;
    private final CourseRepository       courseRepository;
    private final UserRepository         userRepository;
    private final EnrollmentRepository   enrollmentRepository;
    private final BakongApiClient        bakongClient;
    private final BakongProperties       bakongProperties;

    // ─── Step 1: Initiate payment ─────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request, Long studentId) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        if (!course.getIsPublished()) {
            throw new BadRequestException("This course is not available.");
        }

        // Free course — should use /enroll endpoint instead
        if (course.getPrice() == null || course.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("This course is free. Use the enroll endpoint instead.");
        }

        // Already enrolled
        if (enrollmentRepository.existsByCourseIdAndStudentId(request.courseId(), studentId)) {
            throw new BadRequestException("You are already enrolled in this course.");
        }

        // Already has a pending payment for this course — return existing QR
        // so the user doesn't create duplicate orders
        boolean hasPending = paymentRepository.existsByStudent_IdAndCourse_IdAndStatus(
                studentId, request.courseId(), PaymentStatus.PENDING);
        if (hasPending) {
            Payment existing = paymentRepository
                    .findAllByCourse_IdAndStatus(request.courseId(), PaymentStatus.PENDING)
                    .stream()
                    .filter(p -> p.getStudent().getId().equals(studentId))
                    .findFirst()
                    .orElseThrow();
            return toResponse(existing);
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentId));

        String currency = (request.currency() != null) ? request.currency().toUpperCase() : "USD";
        String orderRef = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String memo     = "Coursify: " + course.getTitle();

        // Call Bakong API to generate KHQR
        BakongQrResult qrResult = bakongClient.createQr(
                orderRef, course.getPrice(), currency, memo);

        Payment payment = Payment.builder()
                .orderRef(orderRef)
                .student(student)
                .course(course)
                .amount(course.getPrice())
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .qrCode(qrResult.qrCode())
                .deepLink(qrResult.deepLink())
                .build();

        return toResponse(paymentRepository.save(payment));
    }

    // ─── Step 2a: Bakong webhook ───────────────────────────────────────────────

    /**
     * Called by Bakong's server when a payment is completed.
     *
     * IMPORTANT: This method verifies the HMAC-SHA256 signature that Bakong
     * sends in the X-Signature header before doing anything else. Never skip this.
     *
     * @param orderRef      matches the billNumber we sent when creating the QR
     * @param transactionId Bakong's own transaction ID
     * @param rawPayload    raw request body string (for signature verification)
     * @param signature     value from X-Signature header
     */
    @Override
    @Transactional
    public void handleBakongWebhook(String orderRef, String transactionId,
                                    String rawPayload, String signature) {
        // 1. Verify signature
        if (!isValidSignature(rawPayload, signature)) {
            log.warn("Invalid Bakong webhook signature for orderRef={}", orderRef);
            throw new UnauthorizedException("Invalid webhook signature.");
        }

        // 2. Find our payment record
        Payment payment = paymentRepository.findByOrderRef(orderRef)
                .orElseThrow(() -> {
                    log.warn("Bakong webhook: no payment found for orderRef={}", orderRef);
                    return new ResourceNotFoundException("Payment not found for orderRef: " + orderRef);
                });

        // 3. Idempotency — webhook may be delivered more than once
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.info("Bakong webhook: payment {} already completed, skipping.", orderRef);
            return;
        }

        // 4. Mark payment complete
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Payment completed: orderRef={}, txId={}", orderRef, transactionId);

        // 5. Enroll the student
        enrollAfterPayment(payment);
    }

    // ─── Step 2b: Polling fallback ────────────────────────────────────────────

    /**
     * Frontend polls this endpoint every few seconds while showing the QR.
     * When status becomes COMPLETED the frontend redirects to the course page.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatus(String orderRef, Long studentId) {
        Payment payment = paymentRepository.findByOrderRef(orderRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + orderRef));

        // Security: students can only check their own payments
        if (!payment.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedException("You do not have access to this payment.");
        }

        return toResponse(payment);
    }

    // ─── Payment history ─────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findAllByStudent_Id(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    /**
     * Creates an enrollment record linked to the confirmed payment.
     * Safe to call multiple times — skips if enrollment already exists.
     */
    private void enrollAfterPayment(Payment payment) {
        Long courseId  = payment.getCourse().getId();
        Long studentId = payment.getStudent().getId();

        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            log.info("Enrollment already exists for student={} course={}", studentId, courseId);
            return;
        }

        Enrollment enrollment = Enrollment.builder()
                .course(payment.getCourse())
                .student(payment.getStudent())
                .status(EnrollmentStatus.ACTIVE)
                .payment(payment)
                .build();

        enrollmentRepository.save(enrollment);
        log.info("Enrolled student={} in course={} via payment={}", studentId, courseId, payment.getId());
    }

    /**
     * Verifies HMAC-SHA256 signature sent by Bakong in the X-Signature header.
     * Bakong signs the raw request body with your webhookSecret.
     */
    private boolean isValidSignature(String rawPayload, String receivedSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(
                    bakongProperties.getWebhookSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            mac.init(key);
            byte[] hash = mac.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));
            String expected = HexFormat.of().formatHex(hash);
            return expected.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderRef(),
                payment.getCourse().getId(),
                payment.getCourse().getTitle(),
                payment.getStudent().getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getQrCode(),
                payment.getDeepLink(),
                payment.getCreatedAt()
        );
    }
}