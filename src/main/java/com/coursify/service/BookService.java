package com.coursify.service;

import com.coursify.dto.request.CreateBookRequest;
import com.coursify.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookResponse createBook(CreateBookRequest request, Long userId);
    BookResponse getBookById(Long id);
    Page<BookResponse> getAllBooks(Pageable pageable);
    Page<BookResponse> searchBooks(String keyword, Pageable pageable);
    List<BookResponse> getBooksByCategory(Long categoryId);
    List<BookResponse> getMyBooks(Long userId);
    BookResponse updateBook(Long id, CreateBookRequest request, Long userId);
    void deleteBook(Long id, Long userId);
}