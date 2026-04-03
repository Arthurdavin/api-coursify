package com.coursify.dto.request;

// Request body when user initiates payment for a course
public record InitiatePaymentRequest(
        Long courseId,
        String currency   // "USD" or "KHR" — defaults to USD if null
) {}