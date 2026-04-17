package com.coursify.service;

import com.coursify.dto.response.ChartDataResponse;
import com.coursify.dto.response.DashboardStatsResponse;

import java.util.List;

public interface AdminService {
    DashboardStatsResponse getDashboardStats();
    List<ChartDataResponse.EnrollmentTrend> getEnrollmentTrend();
    List<ChartDataResponse.CategoryDistribution> getCourseDistribution();
    List<ChartDataResponse.TeacherStatus> getTeacherStatus();
}
