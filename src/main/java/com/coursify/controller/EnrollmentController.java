package com.coursify.controller;

import com.coursify.domain.enums.EnrollmentStatus;
import com.coursify.dto.response.EnrollmentResponse;
import com.coursify.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "Enrollment management APIs")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}/enroll/{studentId}")
    @Operation(summary = "Enroll a student in a course")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.enroll(courseId, studentId));
    }

    @PatchMapping("/{enrollmentId}/cancel/{studentId}")
    @Operation(summary = "Cancel an enrollment")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Void> cancel(
            @PathVariable Long enrollmentId,
            @PathVariable Long studentId) {
        enrollmentService.cancelEnrollment(enrollmentId, studentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{enrollmentId}/status")
    @Operation(summary = "Update enrollment status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long enrollmentId,
            @RequestParam EnrollmentStatus status) {
        enrollmentService.updateStatus(enrollmentId, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all enrollments for a student")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all enrollments for a course")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @GetMapping("/check")
    @Operation(summary = "Check if a student is enrolled in a course")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Boolean> isEnrolled(
            @RequestParam Long courseId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(enrollmentService.isEnrolled(courseId, studentId));
    }
}