package com.coursify.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}