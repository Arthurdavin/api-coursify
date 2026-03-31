package com.coursify.dto.response;

public record LessonResponse(
        Long id,
        String title,
        String description,
        String video_url,
        Long course_id
) {}