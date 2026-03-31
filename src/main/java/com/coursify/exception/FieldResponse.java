package com.coursify.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record FieldResponse(
        int status,
        Map<String, String> errors,
        LocalDateTime timestamp
) {
    public FieldResponse(int status, Map<String, String> errors) {
        this(status, errors, LocalDateTime.now());
    }
}
