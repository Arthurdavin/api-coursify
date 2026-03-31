package com.coursify.service;

import com.coursify.dto.request.CreateCourseRequest;
import com.coursify.dto.request.UpdateCourseRequest;
import com.coursify.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CreateCourseRequest request, Long teacherId);
    CourseResponse updateCourse(Long courseId, UpdateCourseRequest request, Long requesterId);
    void deleteCourse(Long courseId, Long requesterId);
    CourseResponse publishCourse(Long courseId, Long requesterId);
    CourseResponse getCourseById(Long courseId);
    Page<CourseResponse> getAllPublishedCourses(Pageable pageable);
    Page<CourseResponse> searchCourses(String keyword, Pageable pageable);
    Page<CourseResponse> getCoursesByCategory(Long categoryId, Pageable pageable);
    List<CourseResponse> getCoursesByTeacher(Long teacherId);
}
