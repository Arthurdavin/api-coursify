package com.coursify.service;

import org.springframework.stereotype.Service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}