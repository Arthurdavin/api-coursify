package com.coursify.service;

import com.coursify.dto.request.InitiatePaymentRequest;
import com.coursify.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    /** Step 1: student clicks "Pay" — creates a pending order and returns QR code */
    PaymentResponse initiatePayment(InitiatePaymentRequest request, Long studentId);

    /** Step 2a: Bakong calls this via webhook when payment is confirmed */
    void handleBakongWebhook(String orderRef, String transactionId, String rawPayload, String signature);

    /** Step 2b: Polling fallback — frontend calls this to check payment status */
    PaymentResponse getPaymentStatus(String orderRef, Long studentId);

    /** History — all payments for a student */
    List<PaymentResponse> getPaymentsByStudent(Long studentId);
}