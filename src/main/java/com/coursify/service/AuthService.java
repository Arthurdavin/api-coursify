package com.coursify.service;

import com.coursify.dto.request.LoginRequest;
import com.coursify.dto.request.RegisterRequest;
import com.coursify.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
