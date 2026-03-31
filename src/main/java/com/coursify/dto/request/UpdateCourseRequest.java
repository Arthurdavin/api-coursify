package com.coursify.dto.request;

import java.math.BigDecimal;

public record UpdateCourseRequest(
        String title,
        String description,
        Long categoryId,
        String imageUrl,
        BigDecimal price,
        Boolean isPublished
) {}
