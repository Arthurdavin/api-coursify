package com.coursify.dto.response;

public record DashboardStatsResponse(
        long totalUsers,
        long totalStudents,
        long totalTeachers,
        long totalCourses,
        long totalEnrollments,
        long publishedCourses
) {}
