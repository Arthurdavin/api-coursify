package com.coursify.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateCourseRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Category ID is required")
        Long category_id,

        String thumbnail,

        @DecimalMin(value = "0.0", message = "Price must be zero or positive")
        BigDecimal price,

        Boolean isPublished,

        List<String> tags,

        // Lessons created together with course
        List<LessonInCourseRequest> lessons
) {}