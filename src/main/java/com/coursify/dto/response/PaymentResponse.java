package com.coursify.dto.response;

import com.coursify.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Returned to frontend after initiating payment — frontend renders qrCode as KHQR image
public record PaymentResponse(
        Long id,
        String orderRef,
        Long courseId,
        String courseTitle,
        Long studentId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String qrCode,      // KHQR string → render as QR image on frontend
        String deepLink,    // optional mobile deep-link
        LocalDateTime createdAt
) {}