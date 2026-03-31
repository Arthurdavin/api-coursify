package com.coursify.exception;

import java.time.LocalDateTime;

public record RestErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public RestErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
