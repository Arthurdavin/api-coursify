package com.coursify.controller;

import com.coursify.domain.User;
import com.coursify.dto.request.CreateCourseRequest;
import com.coursify.dto.request.UpdateCourseRequest;
import com.coursify.dto.response.CourseResponse;
import com.coursify.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // ── Public ──────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all published courses, search by keyword, or filter by category")
    public ResponseEntity<Page<CourseResponse>> getAllCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {

        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(courseService.searchCourses(keyword, pageable));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId, pageable));
        }
        return ResponseEntity.ok(courseService.getAllPublishedCourses(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a course by ID")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // ── Teacher ─────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new course")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(request, currentUser.getId()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a course (only provided fields are updated)")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody UpdateCourseRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, currentUser.getId()));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish a course")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponse> publishCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.publishCourse(id, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        courseService.deleteCourse(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    // ── Teacher: own courses ─────────────────────────────────────────────────

    @GetMapping("/my-courses")
    @Operation(summary = "Get courses created by the current teacher")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(currentUser.getId()));
    }
}