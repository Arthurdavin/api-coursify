package com.coursify.repository;

import com.coursify.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);

    Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    List<Enrollment> findAllByStudentId(Long studentId);

    List<Enrollment> findAllByCourseId(Long courseId);

    void deleteAllByCourseId(Long courseId);

    void deleteAllByStudentId(Long studentId);

    void deleteByCourseIdAndStudentId(Long courseId, Long studentId);
}