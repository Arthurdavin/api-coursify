package com.coursify.repository;

import com.coursify.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    long countByCourseTeacherId(Long teacherId);

    @Query("""
    SELECT FUNCTION('to_char', e.enrolledAt, 'Mon') AS month,
           COUNT(e) AS enrollments
    FROM Enrollment e
    WHERE e.enrolledAt >= :since
    GROUP BY FUNCTION('to_char', e.enrolledAt, 'YYYY-MM'),
             FUNCTION('to_char', e.enrolledAt, 'Mon')
    ORDER BY FUNCTION('to_char', e.enrolledAt, 'YYYY-MM')
""")
    List<Object[]> countEnrollmentsByMonth(@Param("since") LocalDateTime since);
}