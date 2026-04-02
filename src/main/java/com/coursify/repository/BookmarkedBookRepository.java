package com.coursify.repository;

import com.coursify.domain.Book;
import com.coursify.domain.BookmarkedBook;
import com.coursify.domain.BookmarkedBook.BookmarkedBookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkedBookRepository extends JpaRepository<BookmarkedBook, BookmarkedBookId> {
    List<BookmarkedBook> findByUser_Id(Long userId);
    boolean existsByUser_IdAndBook_Id(Long userId, Long bookId);
    void deleteByUser_IdAndBook_Id(Long userId, Long bookId);
    void deleteAllByBook(Book book);
}