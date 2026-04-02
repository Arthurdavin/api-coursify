package com.coursify.dto.request;

import java.util.List;

public record UpdateBookRequest(
        String title,
        String description,
        String file_url,
        String thumbnail,
        List<Long> category_ids
) {}