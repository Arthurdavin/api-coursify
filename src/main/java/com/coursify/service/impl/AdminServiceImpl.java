package com.coursify.service.impl;

import com.coursify.domain.Role;
import com.coursify.dto.response.DashboardStatsResponse;
import com.coursify.repository.CourseRepository;
import com.coursify.repository.EnrollmentRepository;
import com.coursify.repository.UserRepository;
import com.coursify.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers       = userRepository.count();
        long totalStudents    = userRepository.countByRole(Role.STUDENT);
        long totalTeachers    = userRepository.countByRole(Role.TEACHER);
        long totalCourses     = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long publishedCourses = courseRepository.countByIsPublishedTrue();

        return new DashboardStatsResponse(
                totalUsers,
                totalStudents,
                totalTeachers,
                totalCourses,
                totalEnrollments,
                publishedCourses
        );
    }
}
