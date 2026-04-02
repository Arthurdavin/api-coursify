package com.coursify.service;

import com.coursify.dto.request.ForgotPasswordRequest;
import com.coursify.dto.request.ResetPasswordRequest;
import com.coursify.dto.request.VerifyOtpRequest;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequest request);
    void verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
}