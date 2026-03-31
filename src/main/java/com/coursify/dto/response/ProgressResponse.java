package com.coursify.dto.response;

import java.sql.Timestamp;

public record ProgressResponse(
        Long id,
        Long studentId,
        Long courseId,
        String courseTitle,
        Integer progressPercent,
        Timestamp lastAccessedAt
) {}
