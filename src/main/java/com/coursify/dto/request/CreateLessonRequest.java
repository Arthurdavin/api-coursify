package com.coursify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLessonRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        String video_url,

        @NotNull(message = "Course ID is required")
        Long course_id
) {}