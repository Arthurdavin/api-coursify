package com.coursify.service;

import com.coursify.domain.enums.EnrollmentStatus;
import com.coursify.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enroll(Long courseId, Long studentId);
    void cancelEnrollment(Long enrollmentId, Long studentId);
    void updateStatus(Long enrollmentId, EnrollmentStatus status);
    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    boolean isEnrolled(Long courseId, Long studentId);
}