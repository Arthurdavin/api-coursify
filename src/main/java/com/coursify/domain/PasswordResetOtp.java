package com.coursify.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used")
    @Builder.Default
    private Boolean isUsed = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}