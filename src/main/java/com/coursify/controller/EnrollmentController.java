package com.coursify.controller;

import com.coursify.dto.response.EnrollmentResponse;
import com.coursify.service.EnrollmentService;
import com.coursify.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * POST /api/v1/enrollments/{courseId}
     *
     * Enroll the current student in a FREE course.
     * If the course is paid, this returns 400 with a message pointing to /payments/initiate.
     */
    @PostMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enroll(@PathVariable Long courseId) {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(enrollmentService.enroll(courseId, studentId));
    }

    /**
     * DELETE /api/v1/enrollments/{enrollmentId}
     *
     * Student cancels their own enrollment.
     */
    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long enrollmentId) {
        Long studentId = SecurityUtils.getCurrentUserId();
        enrollmentService.cancelEnrollment(enrollmentId, studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/enrollments/my
     *
     * Returns all enrollments for the current student.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments() {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    /**
     * GET /api/v1/enrollments/course/{courseId}
     *
     * Returns all enrollments for a course (teacher/admin only).
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    /**
     * GET /api/v1/enrollments/check/{courseId}
     *
     * Quick check: is the current student enrolled in this course?
     * Frontend uses this to show "Continue learning" vs "Enroll" button.
     */
    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> isEnrolled(@PathVariable Long courseId) {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(enrollmentService.isEnrolled(courseId, studentId));
    }
}