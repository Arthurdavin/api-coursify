package com.coursify.service.impl;

import com.coursify.domain.Course;
import com.coursify.domain.CourseProgress;
import com.coursify.domain.User;
import com.coursify.dto.request.UpdateProgressRequest;
import com.coursify.dto.response.ProgressResponse;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.repository.CourseProgressRepository;
import com.coursify.repository.CourseRepository;
import com.coursify.repository.UserRepository;
import com.coursify.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final CourseProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public ProgressResponse updateProgress(Long courseId, Long studentId, UpdateProgressRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        CourseProgress progress = progressRepository
                .findByStudent_IdAndCourse_Id(studentId, courseId)
                .orElse(CourseProgress.builder().student(student).course(course).build());

        progress.setProgressPercent(request.progressPercent());
        progress.setLastAccessedAt(Timestamp.from(Instant.now()));

        return toResponse(progressRepository.save(progress));
    }

    @Override
    public ProgressResponse getProgress(Long courseId, Long studentId) {
        CourseProgress progress = progressRepository
                .findByStudent_IdAndCourse_Id(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Progress not found for student " + studentId + " and course " + courseId));
        return toResponse(progress);
    }

    @Override
    public List<ProgressResponse> getAllProgressForStudent(Long studentId) {
        return progressRepository.findByStudent_Id(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ProgressResponse toResponse(CourseProgress p) {
        return new ProgressResponse(
                p.getId(),
                p.getStudent().getId(),
                p.getCourse().getId(),
                p.getCourse().getTitle(),
                p.getProgressPercent(),
                p.getLastAccessedAt()
        );
    }
}
