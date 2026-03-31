package com.coursify.controller;

import com.coursify.domain.User;
import com.coursify.dto.request.UpdateProgressRequest;
import com.coursify.dto.response.ProgressResponse;
import com.coursify.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PutMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProgressResponse> updateProgress(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(progressService.updateProgress(courseId, currentUser.getId(), request));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProgressResponse> getProgress(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(progressService.getProgress(courseId, currentUser.getId()));
    }

    @GetMapping("/my-progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ProgressResponse>> getAllMyProgress(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(progressService.getAllProgressForStudent(currentUser.getId()));
    }
}
