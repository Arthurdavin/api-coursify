package com.coursify.dto.request;

public record LessonInCourseRequest(
        String title,
        String description,
        String video_url
) {}