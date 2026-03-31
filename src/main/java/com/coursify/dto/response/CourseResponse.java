package com.coursify.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record CourseResponse(
        Long id,
        String title,
        String description,
        String thumbnail,
        BigDecimal price,
        Boolean isPublished,
        Long teacherId,
        String teacherName,
        Long categoryId,
        String categoryName,
        List<String> tags,
        List<LessonResponse> lessons,
        Timestamp createdAt,
        Timestamp updatedAt
) {}