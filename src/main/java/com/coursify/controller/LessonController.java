//package com.coursify.controller;
//
//import com.coursify.domain.User;
//import com.coursify.dto.request.CreateLessonRequest;
//import com.coursify.dto.response.LessonResponse;
//import com.coursify.service.LessonService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/lessons")
//@RequiredArgsConstructor
//public class LessonController {
//
//    private final LessonService lessonService;
//
//    // ── Get ──────────────────────────────────────────────────────────────────
//
//    @GetMapping("/course/{courseId}")
//    public ResponseEntity<List<LessonResponse>> getLessonsByCourse(
//            @PathVariable Long courseId,
//            @AuthenticationPrincipal User currentUser) {
//        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId, currentUser.getId()));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<LessonResponse> getLesson(
//            @PathVariable Long id,
//            @AuthenticationPrincipal User currentUser) {
//        return ResponseEntity.ok(lessonService.getLessonById(id, currentUser.getId()));
//    }
//
//    // ── Create single lesson ─────────────────────────────────────────────────
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//    public ResponseEntity<LessonResponse> createLesson(
//            @Valid @RequestBody CreateLessonRequest request,
//            @AuthenticationPrincipal User currentUser) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(lessonService.createLesson(request, currentUser.getId()));
//    }
//
//    // ── Create multiple lessons at once ──────────────────────────────────────
//
//    @PostMapping("/batch")
//    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//    public ResponseEntity<List<LessonResponse>> createLessons(
//            @RequestParam Long courseId,
//            @RequestBody List<@Valid CreateLessonRequest> requests,
//            @AuthenticationPrincipal User currentUser) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(lessonService.createLessons(courseId, requests, currentUser.getId()));
//    }
//
//    // ── Update / Delete ──────────────────────────────────────────────────────
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//    public ResponseEntity<LessonResponse> updateLesson(
//            @PathVariable Long id,
//            @RequestBody CreateLessonRequest request,
//            @AuthenticationPrincipal User currentUser) {
//        return ResponseEntity.ok(lessonService.updateLesson(id, request, currentUser.getId()));
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//    public ResponseEntity<Void> deleteLesson(
//            @PathVariable Long id,
//            @AuthenticationPrincipal User currentUser) {
//        lessonService.deleteLesson(id, currentUser.getId());
//        return ResponseEntity.noContent().build();
//    }
//
//    // ── File Upload ──────────────────────────────────────────────────────────
//
//    @PostMapping(value = "/{id}/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//    public ResponseEntity<LessonResponse> uploadVideo(
//            @PathVariable Long id,
//            @RequestParam("file") MultipartFile file,
//            @AuthenticationPrincipal User currentUser,
//            HttpServletRequest httpRequest) {
//        String baseUrl = buildBaseUrl(httpRequest);
//        return ResponseEntity.ok(lessonService.uploadVideo(id, file, currentUser.getId(), baseUrl));
//    }
//
//    @PostMapping(value = "/{id}/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//    public ResponseEntity<LessonResponse> uploadDocument(
//            @PathVariable Long id,
//            @RequestParam("file") MultipartFile file,
//            @AuthenticationPrincipal User currentUser,
//            HttpServletRequest httpRequest) {
//        String baseUrl = buildBaseUrl(httpRequest);
//        return ResponseEntity.ok(lessonService.uploadDocument(id, file, currentUser.getId(), baseUrl));
//    }
//
//    // ── File Download ────────────────────────────────────────────────────────
//
//    @GetMapping("/{id}/download-video")
//    public ResponseEntity<Resource> downloadVideo(
//            @PathVariable Long id,
//            @AuthenticationPrincipal User currentUser) {
//        Resource resource = lessonService.downloadVideo(id, currentUser.getId());
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + resource.getFilename() + "\"")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
//
//    @GetMapping("/{id}/download-document")
//    public ResponseEntity<Resource> downloadDocument(
//            @PathVariable Long id,
//            @AuthenticationPrincipal User currentUser) {
//        Resource resource = lessonService.downloadDocument(id, currentUser.getId());
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + resource.getFilename() + "\"")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
//
//    private String buildBaseUrl(HttpServletRequest request) {
//        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//    }
//}

package com.coursify.controller;

import com.coursify.domain.User;
import com.coursify.dto.request.CreateLessonRequest;
import com.coursify.dto.response.LessonResponse;
import com.coursify.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    // ── Get ──────────────────────────────────────────────────────────────────

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId, currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLesson(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(lessonService.getLessonById(id, currentUser.getId()));
    }

    // ── Create single ────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<LessonResponse> createLesson(
            @Valid @RequestBody CreateLessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(request, currentUser.getId()));
    }

    // ── Create batch ─────────────────────────────────────────────────────────

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<List<LessonResponse>> createLessons(
            @RequestParam Long courseId,
            @RequestBody List<@Valid CreateLessonRequest> requests,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLessons(courseId, requests, currentUser.getId()));
    }

    // ── Update / Delete ──────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long id,
            @RequestBody CreateLessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        lessonService.deleteLesson(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
