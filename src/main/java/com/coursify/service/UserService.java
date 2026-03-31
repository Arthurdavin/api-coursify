package com.coursify.service;

import com.coursify.dto.request.UserUpdateRequest;
import com.coursify.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(Long id);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(String role);
    void deleteUser(Long id);
    UserResponse updateProfile(Long userId, UserUpdateRequest request);
}
