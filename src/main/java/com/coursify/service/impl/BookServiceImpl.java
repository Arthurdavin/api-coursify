package com.coursify.service.impl;

import com.coursify.domain.Book;
import com.coursify.domain.Category;
import com.coursify.domain.Role;
import com.coursify.domain.User;
import com.coursify.dto.request.CreateBookRequest;
import com.coursify.dto.response.BookResponse;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.exception.UnauthorizedException;
import com.coursify.repository.BookRepository;
import com.coursify.repository.CategoryRepository;
import com.coursify.repository.UserRepository;
import com.coursify.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        List<Category> categories = categoryRepository.findAllById(request.category_ids());

        Book book = Book.builder()
                .title(request.title().trim())
                .description(request.description())
                .fileUrl(request.file_url())
                .thumbnail(request.thumbnail())
                .uploadedBy(user)
                .categories(categories)
                .build();

        return toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return toResponse(bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getMyBooks(Long userId) {
        return bookRepository.findByUploadedBy_Id(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, CreateBookRequest request, Long userId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        assertOwnerOrAdmin(book, userId);

        book.setTitle(request.title().trim());
        if (request.description() != null) book.setDescription(request.description());
        if (request.file_url() != null) book.setFileUrl(request.file_url());
        if (request.thumbnail() != null) book.setThumbnail(request.thumbnail());
        if (request.category_ids() != null) {
            book.setCategories(categoryRepository.findAllById(request.category_ids()));
        }

        return toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long id, Long userId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        assertOwnerOrAdmin(book, userId);
        bookRepository.delete(book);
    }

    private void assertOwnerOrAdmin(Book book, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (user.getRole() == Role.ADMIN) return;
        if (!book.getUploadedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to modify this book");
        }
    }

    private BookResponse toResponse(Book book) {
        List<String> categoryNames = book.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getFileUrl(),
                book.getThumbnail(),
                book.getUploadedBy().getId(),
                book.getUploadedBy().getFirstName() + " " + book.getUploadedBy().getLastName(),
                categoryNames,
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}