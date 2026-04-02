package com.coursify.service.impl;

import com.coursify.domain.*;
import com.coursify.dto.request.CreateLessonRequest;
import com.coursify.dto.response.LessonResponse;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.exception.UnauthorizedException;
import com.coursify.repository.*;
import com.coursify.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    // ── Create single ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request, Long teacherId) {
        Course course = getCourseOrThrow(request.course_id());
        assertTeacherOwnsOrAdmin(course, teacherId);

        Lesson lesson = Lesson.builder()
                .title(request.title())
                .description(request.description())
                .videoUrl(request.video_url())
                .course(course)
                .build();

        return toResponse(lessonRepository.save(lesson), true);
    }

    // ── Create batch ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<LessonResponse> createLessons(Long courseId, List<CreateLessonRequest> requests, Long teacherId) {
        Course course = getCourseOrThrow(courseId);
        assertTeacherOwnsOrAdmin(course, teacherId);

        List<Lesson> lessons = requests.stream().map(req -> Lesson.builder()
                .title(req.title())
                .description(req.description())
                .videoUrl(req.video_url())
                .course(course)
                .build()
        ).collect(Collectors.toList());

        return lessonRepository.saveAll(lessons).stream()
                .map(l -> toResponse(l, true))
                .collect(Collectors.toList());
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, CreateLessonRequest request, Long teacherId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        assertTeacherOwnsOrAdmin(lesson.getCourse(), teacherId);

        lesson.setTitle(request.title());
        if (request.description() != null) lesson.setDescription(request.description());
        if (request.video_url() != null) lesson.setVideoUrl(request.video_url());

        return toResponse(lessonRepository.save(lesson), true);
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteLesson(Long lessonId, Long teacherId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        assertTeacherOwnsOrAdmin(lesson.getCourse(), teacherId);
        lessonRepository.delete(lesson);
    }

    // ── Get ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourse(Long courseId, Long callerId) {
        boolean canSeeContent = isEnrolledOrTeacher(courseId, callerId);
        return lessonRepository.findByCourse_Id(courseId)
                .stream()
                .map(l -> toResponse(l, canSeeContent))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long lessonId, Long callerId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        boolean canSeeContent = isEnrolledOrTeacher(lesson.getCourse().getId(), callerId);
        return toResponse(lesson, canSeeContent);
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private boolean isEnrolledOrTeacher(Long courseId, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.TEACHER) return true;
        return enrollmentRepository.existsByCourseIdAndStudentId(userId, courseId);
    }

    private Course getCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
    }

    private Lesson getLessonOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));
    }

    private void assertTeacherOwnsOrAdmin(Course course, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (user.getRole() == Role.ADMIN) return;
        if (!course.getTeacher().getId().equals(userId))
            throw new UnauthorizedException("You do not own this course");
    }

    private LessonResponse toResponse(Lesson lesson, boolean includeContent) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                includeContent ? lesson.getVideoUrl() : null,
                lesson.getCourse() != null ? lesson.getCourse().getId() : null
        );
    }
}