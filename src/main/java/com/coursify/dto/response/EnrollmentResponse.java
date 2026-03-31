package com.coursify.dto.response;

import java.sql.Timestamp;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseTitle,
        String status,
        Timestamp enrolledAt
) {}
