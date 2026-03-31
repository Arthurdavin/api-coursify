package com.coursify.controller;

import com.coursify.domain.User;
import com.coursify.dto.request.EnrollRequest;
import com.coursify.dto.response.EnrollmentResponse;
import com.coursify.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody EnrollRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enroll(request, currentUser.getId()));
    }

    @GetMapping("/my-enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(currentUser.getId()));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> enrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkEnrollment(
            @RequestParam Long courseId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(enrollmentService.isEnrolled(currentUser.getId(), courseId));
    }
}
