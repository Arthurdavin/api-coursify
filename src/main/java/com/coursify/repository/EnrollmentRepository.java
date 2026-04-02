package com.coursify.repository;

import com.coursify.domain.Enrollment;
import com.coursify.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {


    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);

    List<Enrollment> findAllByStudentId(Long studentId);

    List<Enrollment> findAllByCourseId(Long courseId);

    Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    List<Enrollment> findAllByStatus(EnrollmentStatus status);

    void deleteAllByStudentId(Long studentId); // 👈 add this

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    long countActiveByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'ACTIVE'")
    long countActiveByStudentId(@Param("studentId") Long studentId);
    void deleteAllByCourseId(Long courseId);
}