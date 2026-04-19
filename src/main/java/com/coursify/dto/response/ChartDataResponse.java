package com.coursify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

public class ChartDataResponse {

    @Data
    @AllArgsConstructor
    public static class EnrollmentTrend {
        private String month;
        private long enrollments;
    }

    @Data
    @AllArgsConstructor
    public static class CategoryDistribution {
        private String name;
        private long value;
    }

    @Data
    @AllArgsConstructor
    public static class TeacherStatus {
        private String name;
        private long value;
    }
}