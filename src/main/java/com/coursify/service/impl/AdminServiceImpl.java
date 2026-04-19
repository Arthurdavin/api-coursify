//package com.coursify.service.impl;
//
//import com.coursify.domain.Role;
//import com.coursify.dto.response.DashboardStatsResponse;
//import com.coursify.repository.CourseRepository;
//import com.coursify.repository.EnrollmentRepository;
//import com.coursify.repository.UserRepository;
//import com.coursify.service.AdminService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AdminServiceImpl implements AdminService {
//
//    private final UserRepository userRepository;
//    private final CourseRepository courseRepository;
//    private final EnrollmentRepository enrollmentRepository;
//
//    @Override
//    public DashboardStatsResponse getDashboardStats() {
//        long totalUsers       = userRepository.count();
//        long totalStudents    = userRepository.countByRole(Role.STUDENT);
//        long totalTeachers    = userRepository.countByRole(Role.TEACHER);
//        long totalCourses     = courseRepository.count();
//        long totalEnrollments = enrollmentRepository.count();
//        long publishedCourses = courseRepository.countByIsPublishedTrue();
//
//        return new DashboardStatsResponse(
//                totalUsers,
//                totalStudents,
//                totalTeachers,
//                totalCourses,
//                totalEnrollments,
//                publishedCourses
//        );
//    }
//}


package com.coursify.service.impl;

import com.coursify.domain.Role;
import com.coursify.dto.response.ChartDataResponse;
import com.coursify.dto.response.DashboardStatsResponse;
import com.coursify.repository.CourseRepository;
import com.coursify.repository.EnrollmentRepository;
import com.coursify.repository.UserRepository;
import com.coursify.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

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
                totalUsers, totalStudents, totalTeachers,
                totalCourses, totalEnrollments, publishedCourses
        );
    }

    @Override
    public List<ChartDataResponse.EnrollmentTrend> getEnrollmentTrend() {
        LocalDateTime since = LocalDateTime.now().minusMonths(6);
        List<Object[]> rows = enrollmentRepository.countEnrollmentsByMonth(since);
        return rows.stream()
                .map(r -> new ChartDataResponse.EnrollmentTrend(
                        (String) r[0],                          // month label e.g. "Jan"
                        ((Number) r[1]).longValue()             // count
                ))
                .toList();
    }

    @Override
    public List<ChartDataResponse.CategoryDistribution> getCourseDistribution() {
        List<Object[]> rows = courseRepository.countByCategory();
        return rows.stream()
                .map(r -> new ChartDataResponse.CategoryDistribution(
                        (String) r[0],                          // category name
                        ((Number) r[1]).longValue()             // count
                ))
                .toList();
    }

    @Override
    public List<ChartDataResponse.TeacherStatus> getTeacherStatus() {
        long active   = userRepository.countByRole(Role.TEACHER);  // adjust if you have isActive field
        long students = userRepository.countByRole(Role.STUDENT);
        long total    = userRepository.count();

        return List.of(
                new ChartDataResponse.TeacherStatus("Teachers", active),
                new ChartDataResponse.TeacherStatus("Students", students),
                new ChartDataResponse.TeacherStatus("Total",    total)
        );
    }
}