package com.coursify.dto.response;

import com.coursify.domain.Role;
import java.sql.Timestamp;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String gender,
        Role role,
        String imageUrl,
        Timestamp createdAt
) {}
