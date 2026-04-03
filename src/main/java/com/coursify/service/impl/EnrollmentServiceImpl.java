package com.coursify.service.impl;

import com.coursify.domain.Course;
import com.coursify.domain.Enrollment;
import com.coursify.domain.User;
import com.coursify.domain.enums.EnrollmentStatus;
import com.coursify.dto.response.EnrollmentResponse;
import com.coursify.exception.BadRequestException;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.repository.CourseRepository;
import com.coursify.repository.EnrollmentRepository;
import com.coursify.repository.UserRepository;
import com.coursify.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository     courseRepository;
    private final UserRepository       userRepository;

    /**
     * Enroll a student in a FREE course only.
     * Paid courses must go through PaymentService → Bakong → webhook → enrollAfterPayment.
     */
    @Override
    @Transactional
    public EnrollmentResponse enroll(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Must be published
        if (!course.getIsPublished()) {
            throw new BadRequestException("This course is not currently available.");
        }

        // Block paid courses — they must go through payment flow
        if (course.getPrice() != null && course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException(
                    "This course requires payment. Please use the payment endpoint.");
        }

        // Duplicate check
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new BadRequestException("You are already enrolled in this course.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentId));

        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .student(student)
                .status(EnrollmentStatus.ACTIVE)
                .payment(null)  // no payment for free courses
                .build();

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("You are not authorized to cancel this enrollment.");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void updateStatus(Long enrollmentId, EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        enrollment.setStatus(status);
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findAllByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findAllByCourseId(courseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEnrolled(Long courseId, Long studentId) {
        return enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId);
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getUsername(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name(),
                enrollment.getEnrolledAt()
        );
    }
}