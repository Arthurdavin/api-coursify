package com.coursify.service;

import com.coursify.domain.enums.EnrollmentStatus;
import com.coursify.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    /** Enroll a student in a FREE course. Paid courses are blocked here. */
    EnrollmentResponse enroll(Long courseId, Long studentId);

    /** Student cancels their own enrollment */
    void cancelEnrollment(Long enrollmentId, Long studentId);

    /** Admin/teacher updates enrollment status */
    void updateStatus(Long enrollmentId, EnrollmentStatus status);

    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);

    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);

    boolean isEnrolled(Long courseId, Long studentId);
}