package com.coursify.dto.response;

import java.sql.Timestamp;
import java.util.List;

public record BookResponse(
        Long id,
        String title,
        String description,
        String fileUrl,
        String thumbnail,
        Long uploadedById,
        String uploadedByName,
        List<String> categories,
        Timestamp createdAt,
        Timestamp updatedAt
) {}