package com.coursify.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "bookmarked_books")
@IdClass(BookmarkedBook.BookmarkedBookId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkedBook {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkedBookId implements Serializable {
        private Long user;
        private Long book;
    }
}