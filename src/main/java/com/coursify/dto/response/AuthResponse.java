package com.coursify.dto.response;

import com.coursify.domain.Role;

import java.sql.Timestamp;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String email,
        Role role
) {}
