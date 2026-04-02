package com.coursify.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCourseRequest(
        String title,
        String description,
        String imageUrl,
        BigDecimal price,
        Boolean isPublished,
        Long categoryId,
        List<String> tags
) {}