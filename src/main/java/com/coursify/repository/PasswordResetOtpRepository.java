package com.coursify.repository;

import com.coursify.domain.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmailAndOtpAndIsUsedFalse(String email, String otp);

    @Modifying
    @Query("DELETE FROM PasswordResetOtp o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);
}