package com.coursify.controller;

import com.coursify.dto.request.InitiatePaymentRequest;
import com.coursify.dto.response.PaymentResponse;
import com.coursify.service.PaymentService;
import com.coursify.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/v1/payments/initiate
     *
     * Student initiates payment for a paid course.
     * Returns QR code string → frontend renders as KHQR image.
     *
     * Example response:
     * {
     *   "orderRef": "abc123...",
     *   "qrCode": "00020101021229...",
     *   "deepLink": "bakong://...",
     *   "status": "PENDING"
     * }
     */
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestBody InitiatePaymentRequest request) {

        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentService.initiatePayment(request, studentId));
    }

    /**
     * GET /api/v1/payments/status/{orderRef}
     *
     * Frontend polls this every 3s while showing QR to the user.
     * When status == "COMPLETED", redirect to course page.
     */
    @GetMapping("/status/{orderRef}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResponse> getPaymentStatus(
            @PathVariable String orderRef) {

        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentService.getPaymentStatus(orderRef, studentId));
    }

    /**
     * GET /api/v1/payments/my
     *
     * Returns all payments made by the current student.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<PaymentResponse>> myPayments() {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentService.getPaymentsByStudent(studentId));
    }

    /**
     * POST /api/v1/payments/webhook/bakong
     *
     * Bakong calls this endpoint after a KHQR payment is completed.
     * MUST be publicly accessible (no auth) — Bakong's server won't send a JWT.
     * Security is handled via HMAC-SHA256 signature verification inside the service.
     *
     * Headers Bakong sends:
     *   X-Signature: <hmac-sha256 of raw body using your webhookSecret>
     *
     * Body example:
     * {
     *   "transactionId": "TXN123456",
     *   "billNumber": "abc123...",   ← this is our orderRef
     *   "amount": 25.00,
     *   "currency": 840,
     *   "status": "SUCCESS"
     * }
     *
     * IMPORTANT: Register this URL in your Bakong merchant portal.
     * For local testing use ngrok: ngrok http 8080
     */
    @PostMapping("/webhook/bakong")
    public ResponseEntity<Void> bakongWebhook(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        log.info("Received Bakong webhook payload: {}", rawPayload);

        // Parse the two fields we need from the raw JSON
        // Using simple string parse to avoid Jackson dependency on raw body
        String orderRef       = extractJsonField(rawPayload, "billNumber");
        String transactionId  = extractJsonField(rawPayload, "transactionId");

        paymentService.handleBakongWebhook(orderRef, transactionId, rawPayload, signature);

        // Always return 200 to Bakong so it doesn't retry unnecessarily
        return ResponseEntity.ok().build();
    }

    /**
     * Very lightweight JSON field extractor — avoids double-parsing the body.
     * For a production system you can use @RequestBody Map<String,Object> instead
     * and keep the raw body in a filter for signature verification.
     */
    private String extractJsonField(String json, String field) {
        String search = "\"" + field + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int colon = json.indexOf(":", idx);
        int start = json.indexOf("\"", colon) + 1;
        int end   = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}