//package com.coursify.controller;
//
//import com.coursify.dto.response.DashboardStatsResponse;
//import com.coursify.service.AdminService;
//import com.coursify.service.CourseService;
//import com.coursify.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    private final AdminService adminService;
//    private final UserService userService;
//    private final CourseService courseService;
//
//    @GetMapping("/stats")
//    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
//        return ResponseEntity.ok(adminService.getDashboardStats());
//    }
//
//    @DeleteMapping("/users/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/courses/{id}")
//    public ResponseEntity<Void> deleteCourse(
//            @PathVariable Long id,
//            @org.springframework.security.core.annotation.AuthenticationPrincipal com.coursify.domain.User currentUser) {
//        courseService.deleteCourse(id, currentUser.getId());
//        return ResponseEntity.noContent().build();
//    }
//}


package com.coursify.controller;

import com.coursify.domain.User;
import com.coursify.dto.request.CreateBookRequest;
import com.coursify.dto.request.UpdateBookRequest;
import com.coursify.dto.response.BookResponse;
import com.coursify.dto.response.DashboardStatsResponse;
import com.coursify.service.AdminService;
import com.coursify.service.BookService;
import com.coursify.service.CourseService;
import com.coursify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final CourseService courseService;
    private final BookService bookService;

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ── User Management ──────────────────────────────────────────────────────

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ── Course Management ────────────────────────────────────────────────────

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        courseService.deleteCourse(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    // ── Book Management ──────────────────────────────────────────────────────

    @GetMapping("/books")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(bookService.searchBooks(keyword, pageable));
        }
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(
            @RequestBody CreateBookRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(request, currentUser.getId()));
    }

    @PatchMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestBody UpdateBookRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(bookService.updateBook(id, request, currentUser.getId()));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        bookService.deleteBook(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}