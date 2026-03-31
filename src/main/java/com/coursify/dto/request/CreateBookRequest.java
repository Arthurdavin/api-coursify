package com.coursify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateBookRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "File URL is required")
        String file_url,

        String thumbnail,

        @NotEmpty(message = "At least one category is required")
        List<Long> category_ids
) {}