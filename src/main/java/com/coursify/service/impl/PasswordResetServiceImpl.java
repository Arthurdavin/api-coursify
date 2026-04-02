package com.coursify.service.impl;

import com.coursify.domain.PasswordResetOtp;
import com.coursify.dto.request.ForgotPasswordRequest;
import com.coursify.dto.request.ResetPasswordRequest;
import com.coursify.dto.request.VerifyOtpRequest;
import com.coursify.exception.ResourceNotFoundException;
import com.coursify.repository.PasswordResetOtpRepository;
import com.coursify.repository.UserRepository;
import com.coursify.service.EmailService;
import com.coursify.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;  // ← use SecureRandom, not Random
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();  // ← secure

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + request.email()));

        otpRepository.deleteAllByEmail(request.email());

        // ← SecureRandom + correct range for always 6 digits (000000–999999)
        String otp = String.format("%06d", secureRandom.nextInt(1000000));

        otpRepository.save(PasswordResetOtp.builder()
                .email(request.email())
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .build());

        emailService.sendOtpEmail(request.email(), otp);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyOtp(VerifyOtpRequest request) {
        PasswordResetOtp otpEntity = otpRepository
                .findByEmailAndOtpAndIsUsedFalse(request.email(), request.otp())
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (otpEntity.isExpired()) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetOtp otpEntity = otpRepository
                .findByEmailAndOtpAndIsUsedFalse(request.email(), request.otp())
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (otpEntity.isExpired()) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + request.email()));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        otpEntity.setIsUsed(true);
        otpRepository.save(otpEntity);
    }
}