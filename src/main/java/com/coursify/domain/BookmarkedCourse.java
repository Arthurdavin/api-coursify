package com.coursify.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "bookmarked_courses")
@IdClass(BookmarkedCourse.BookmarkedCourseId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkedCourse {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkedCourseId implements Serializable {
        private Long user;
        private Long course;
    }
}
