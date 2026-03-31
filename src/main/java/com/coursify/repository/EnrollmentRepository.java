package com.coursify.repository;

import com.coursify.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent_Id(Long studentId);
    List<Enrollment> findByCourse_Id(Long courseId);
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    Optional<Enrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    long count();
}
