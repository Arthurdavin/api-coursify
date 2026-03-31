package com.coursify.service;

import com.coursify.dto.request.UpdateProgressRequest;
import com.coursify.dto.response.ProgressResponse;

import java.util.List;

public interface ProgressService {
    ProgressResponse updateProgress(Long courseId, Long studentId, UpdateProgressRequest request);
    ProgressResponse getProgress(Long courseId, Long studentId);
    List<ProgressResponse> getAllProgressForStudent(Long studentId);
}
