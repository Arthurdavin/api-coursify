//package com.coursify.controller;
//
//import com.coursify.dto.request.LoginRequest;
//import com.coursify.dto.request.RegisterRequest;
//import com.coursify.dto.response.AuthResponse;
//import com.coursify.service.AuthService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//        return ResponseEntity.ok(authService.login(request));
//    }
//}

package com.coursify.controller;

import com.coursify.dto.request.*;
import com.coursify.dto.response.AuthResponse;
import com.coursify.service.AuthService;
import com.coursify.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ── Forgot Password Flow ─────────────────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to " + request.email() + ". Check your inbox.",
                "email", request.email()
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        passwordResetService.verifyOtp(request);
        return ResponseEntity.ok(Map.of(
                "message", "OTP verified successfully. You can now reset your password.",
                "email", request.email()
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully. Please login with your new password."
        ));
    }
}
