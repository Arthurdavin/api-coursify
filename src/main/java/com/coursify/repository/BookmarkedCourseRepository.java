package com.coursify.repository;

import com.coursify.domain.BookmarkedCourse;
import com.coursify.domain.BookmarkedCourse.BookmarkedCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkedCourseRepository extends JpaRepository<BookmarkedCourse, BookmarkedCourseId> {
    List<BookmarkedCourse> findByUser_Id(Long userId);
    boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);
    void deleteByUser_IdAndCourse_Id(Long userId, Long courseId);
}
