package com.coursify.service;

import com.coursify.dto.request.EnrollRequest;
import com.coursify.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enroll(EnrollRequest request, Long studentId);
    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    boolean isEnrolled(Long studentId, Long courseId);
}
