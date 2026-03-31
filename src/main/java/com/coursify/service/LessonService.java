package com.coursify.service;

import com.coursify.dto.request.CreateLessonRequest;
import com.coursify.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {
    LessonResponse createLesson(CreateLessonRequest request, Long teacherId);
    List<LessonResponse> createLessons(Long courseId, List<CreateLessonRequest> requests, Long teacherId);
    LessonResponse updateLesson(Long lessonId, CreateLessonRequest request, Long teacherId);
    void deleteLesson(Long lessonId, Long teacherId);
    List<LessonResponse> getLessonsByCourse(Long courseId, Long callerId);
    LessonResponse getLessonById(Long lessonId, Long callerId);
}