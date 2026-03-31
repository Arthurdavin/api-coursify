package com.coursify.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollRequest(
        @NotNull(message = "Course ID is required")
        Long courseId
) {}
