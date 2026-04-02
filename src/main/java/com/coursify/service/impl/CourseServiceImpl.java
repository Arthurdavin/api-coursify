package com.coursify.service.impl;

import com.coursify.domain.*;
import com.coursify.dto.request.CreateCourseRequest;
import com.coursify.dto.request.UpdateCourseRequest;
import com.coursify.dto.response.CourseResponse;
import com.coursify.dto.response.LessonResponse;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.exception.UnauthorizedException;
import com.coursify.repository.*;
import com.coursify.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;           // 👈 add
    private final BookmarkedCourseRepository bookmarkedCourseRepository; // 👈 add

    @Override
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", teacherId));

        Category category = categoryRepository.findById(request.category_id())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.category_id()));

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .imageUrl(request.thumbnail())
                .price(request.price())
                .isPublished(request.isPublished() != null && request.isPublished())
                .teacher(teacher)
                .category(category)
                .build();

        courseRepository.save(course);

        if (request.tags() != null && !request.tags().isEmpty()) {
            List<CourseTag> courseTags = new ArrayList<>();
            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName.trim())
                        .orElseGet(() -> tagRepository.save(
                                Tag.builder().name(tagName.trim()).build()));
                courseTags.add(CourseTag.builder().course(course).tag(tag).build());
            }
            course.setCourseTags(courseTags);
        }

        if (request.lessons() != null && !request.lessons().isEmpty()) {
            List<Lesson> lessons = request.lessons().stream().map(l ->
                    Lesson.builder()
                            .title(l.title())
                            .description(l.description())
                            .videoUrl(l.video_url())
                            .course(course)
                            .build()
            ).collect(Collectors.toList());
            lessonRepository.saveAll(lessons);
            course.setLessons(lessons);
        }

        courseRepository.save(course);
        return toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request, Long requesterId) {
        Course course = getCourseOrThrow(courseId);
        assertOwnerOrAdmin(course, requesterId);

        if (request.title() != null) course.setTitle(request.title());
        if (request.description() != null) course.setDescription(request.description());
        if (request.imageUrl() != null) course.setImageUrl(request.imageUrl());
        if (request.price() != null) course.setPrice(request.price());
        if (request.isPublished() != null) course.setIsPublished(request.isPublished());

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
            course.setCategory(category);
        }

        if (request.tags() != null && !request.tags().isEmpty()) {
            List<CourseTag> courseTags = new ArrayList<>();
            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName.trim())
                        .orElseGet(() -> tagRepository.save(
                                Tag.builder().name(tagName.trim()).build()));
                courseTags.add(CourseTag.builder().course(course).tag(tag).build());
            }
            course.setCourseTags(courseTags);
        }

        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId, Long requesterId) {
        Course course = getCourseOrThrow(courseId);
        assertOwnerOrAdmin(course, requesterId);

        // Delete dependent records first to avoid FK violations
        bookmarkedCourseRepository.deleteAllByCourseId(courseId); // 👈
        enrollmentRepository.deleteAllByCourseId(courseId);       // 👈
        lessonRepository.deleteAllByCourseId(courseId);           // 👈

        courseRepository.delete(course);
    }

    @Override
    @Transactional
    public CourseResponse publishCourse(Long courseId, Long requesterId) {
        Course course = getCourseOrThrow(courseId);
        assertOwnerOrAdmin(course, requesterId);
        course.setIsPublished(true);
        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long courseId) {
        return toResponse(getCourseOrThrow(courseId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllPublishedCourses(Pageable pageable) {
        return courseRepository.findByIsPublishedTrue(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchCourses(String keyword, Pageable pageable) {
        return courseRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCoursesByCategory(Long categoryId, Pageable pageable) {
        return courseRepository.findPublishedByCategory(categoryId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacher_Id(teacherId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Course getCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
    }

    private void assertOwnerOrAdmin(Course course, Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", requesterId));
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        boolean isOwner = course.getTeacher().getId().equals(requesterId);
        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("You do not have permission to modify this course");
        }
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getVideoUrl(),
                lesson.getCourse() != null ? lesson.getCourse().getId() : null
        );
    }

    private CourseResponse toResponse(Course course) {
        List<String> tags = course.getCourseTags().stream()
                .map(ct -> ct.getTag().getName())
                .collect(Collectors.toList());

        List<LessonResponse> lessons = course.getLessons().stream()
                .map(this::toLessonResponse)
                .collect(Collectors.toList());

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getImageUrl(),
                course.getPrice(),
                course.getIsPublished(),
                course.getTeacher().getId(),
                course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName(),
                course.getCategory() != null ? course.getCategory().getId() : null,
                course.getCategory() != null ? course.getCategory().getName() : null,
                tags,
                lessons,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}