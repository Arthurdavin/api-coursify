package com.coursify.controller;

import com.coursify.domain.*;
import com.coursify.dto.response.BookResponse;
import com.coursify.dto.response.CourseResponse;
import com.coursify.exception.DuplicateResourceException;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.repository.*;
import com.coursify.service.BookService;
import com.coursify.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkedCourseRepository bookmarkedCourseRepository;
    private final BookmarkedBookRepository bookmarkedBookRepository;
    private final CourseRepository courseRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final BookService bookService;

    // ── Course Bookmarks ─────────────────────────────────────────────────────

    @GetMapping("/courses")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CourseResponse>> getBookmarkedCourses(
            @AuthenticationPrincipal User currentUser) {
        List<CourseResponse> courses = bookmarkedCourseRepository
                .findByUser_Id(currentUser.getId())
                .stream()
                .map(bc -> courseService.getCourseById(bc.getCourse().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/courses/{courseId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> bookmarkCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {

        if (bookmarkedCourseRepository.existsByUser_IdAndCourse_Id(currentUser.getId(), courseId)) {
            throw new DuplicateResourceException("Course already bookmarked");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUser.getId()));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        bookmarkedCourseRepository.save(
                BookmarkedCourse.builder().user(user).course(course).build()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Course bookmarked successfully",
                "courseId", courseId,
                "bookmarked", true
        ));
    }

    @DeleteMapping("/courses/{courseId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> removeBookmarkCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {

        if (!bookmarkedCourseRepository.existsByUser_IdAndCourse_Id(currentUser.getId(), courseId)) {
            throw new ResourceNotFoundException("Bookmark not found for course " + courseId);
        }

        bookmarkedCourseRepository.deleteByUser_IdAndCourse_Id(currentUser.getId(), courseId);

        return ResponseEntity.ok(Map.of(
                "message", "Course bookmark removed",
                "courseId", courseId,
                "bookmarked", false
        ));
    }

    @GetMapping("/courses/{courseId}/check")
    public ResponseEntity<Map<String, Object>> checkCourseBookmark(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        boolean bookmarked = bookmarkedCourseRepository
                .existsByUser_IdAndCourse_Id(currentUser.getId(), courseId);
        return ResponseEntity.ok(Map.of("courseId", courseId, "bookmarked", bookmarked));
    }

    // ── Book Bookmarks ───────────────────────────────────────────────────────

    @GetMapping("/books")
    @Transactional(readOnly = true)
    public ResponseEntity<List<BookResponse>> getBookmarkedBooks(
            @AuthenticationPrincipal User currentUser) {
        List<BookResponse> books = bookmarkedBookRepository
                .findByUser_Id(currentUser.getId())
                .stream()
                .map(bb -> bookService.getBookById(bb.getBook().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @PostMapping("/books/{bookId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> bookmarkBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser) {

        if (bookmarkedBookRepository.existsByUser_IdAndBook_Id(currentUser.getId(), bookId)) {
            throw new DuplicateResourceException("Book already bookmarked");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUser.getId()));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        bookmarkedBookRepository.save(
                BookmarkedBook.builder().user(user).book(book).build()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Book bookmarked successfully",
                "bookId", bookId,
                "bookmarked", true
        ));
    }

    @DeleteMapping("/books/{bookId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> removeBookmarkBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser) {

        if (!bookmarkedBookRepository.existsByUser_IdAndBook_Id(currentUser.getId(), bookId)) {
            throw new ResourceNotFoundException("Bookmark not found for book " + bookId);
        }

        bookmarkedBookRepository.deleteByUser_IdAndBook_Id(currentUser.getId(), bookId);

        return ResponseEntity.ok(Map.of(
                "message", "Book bookmark removed",
                "bookId", bookId,
                "bookmarked", false
        ));
    }

    @GetMapping("/books/{bookId}/check")
    public ResponseEntity<Map<String, Object>> checkBookBookmark(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser) {
        boolean bookmarked = bookmarkedBookRepository
                .existsByUser_IdAndBook_Id(currentUser.getId(), bookId);
        return ResponseEntity.ok(Map.of("bookId", bookId, "bookmarked", bookmarked));
    }
}