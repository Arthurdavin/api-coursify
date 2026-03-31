package com.coursify.repository;

import com.coursify.domain.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
    Optional<CourseProgress> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    List<CourseProgress> findByStudent_Id(Long studentId);
}
