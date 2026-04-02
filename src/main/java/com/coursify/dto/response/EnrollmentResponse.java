package com.coursify.dto.response;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseTitle,
        String status,
        LocalDateTime enrolledAt
) {}