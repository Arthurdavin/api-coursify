package com.coursify.repository;

import com.coursify.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
//public interface CourseRepository extends JpaRepository<Course, Long> {
//
//    List<Course> findByTeacher_Id(Long teacherId);
//
//    List<Course> findByCategory_Id(Long categoryId);
//
//    Page<Course> findByIsPublishedTrue(Pageable pageable);
//
////    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND " +
////           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
////           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//// CourseRepository
//@Query("SELECT c FROM Course c WHERE " +
//        "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//        "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//Page<Course> searchAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
//    Page<Course> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
//
//    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND c.category.id = :categoryId")
//    Page<Course> findPublishedByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
//
//    long countByIsPublishedTrue();
//
//    Page<Course> findAll(Pageable pageable);
//}

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacher_Id(Long teacherId);

    List<Course> findByCategory_Id(Long categoryId);

    Page<Course> findByIsPublishedTrue(Pageable pageable);

    // For public/student use — published only
    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // For admin use — all courses regardless of publish status
    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND c.category.id = :categoryId")
    Page<Course> findPublishedByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    long countByIsPublishedTrue();

    @Query("SELECT c.category.name, COUNT(c) FROM Course c GROUP BY c.category.name ORDER BY COUNT(c) DESC")
    List<Object[]> countByCategory();
    // findAll(Pageable) is already inherited from JpaRepository — remove this line
    // Page<Course> findAll(Pageable pageable);
}
