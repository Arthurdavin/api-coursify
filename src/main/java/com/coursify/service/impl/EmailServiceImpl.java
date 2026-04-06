//package com.coursify.service.impl;
//
//import com.coursify.service.EmailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//@Service
//@RequiredArgsConstructor
//public class EmailServiceImpl implements EmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String fromEmail;
//
//    @Override
//    public void sendOtpEmail(String toEmail, String otp) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);  // ← required by Gmail
//            helper.setTo(toEmail);
//            helper.setSubject("Coursify — Password Reset OTP");
//            helper.setText(buildEmailHtml(otp), true);
//
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email: " + e.getMessage());
//        }
//    }
//
//    private String buildEmailHtml(String otp) {
//        return """
//                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto; padding: 32px; border: 1px solid #e0e0e0; border-radius: 12px;">
//                  <h2 style="color: #1a1a2e;">🔐 Password Reset</h2>
//                  <p style="color: #555;">You requested to reset your Coursify password. Use the OTP below:</p>
//                  <div style="background: #f4f4f4; border-radius: 8px; padding: 24px; text-align: center; margin: 24px 0;">
//                    <span style="font-size: 40px; font-weight: bold; letter-spacing: 10px; color: #1a1a2e;">%s</span>
//                  </div>
//                  <p style="color: #555;">This OTP expires in <strong>10 minutes</strong>.</p>
//                  <p style="color: #999; font-size: 13px;">If you did not request this, please ignore this email.</p>
//                  <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;">
//                  <p style="color: #999; font-size: 12px; text-align: center;">© 2024 Coursify. All rights reserved.</p>
//                </div>
//                """.formatted(otp);
//    }
//}

package com.coursify.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.coursify.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            Resend resend = new Resend(resendApiKey);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Coursify <onboarding@resend.dev>")
                    .to(toEmail)
                    .subject("Coursify — Password Reset OTP")
                    .html(buildEmailHtml(otp))
                    .build();

            resend.emails().send(params);
            log.info("OTP email sent to {}", toEmail);

        } catch (ResendException e) {
            log.error("Failed to send email via Resend", e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String buildEmailHtml(String otp) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto; padding: 32px; border: 1px solid #e0e0e0; border-radius: 12px;">
                  <h2 style="color: #1a1a2e;">🔐 Password Reset</h2>
                  <p style="color: #555;">You requested to reset your Coursify password. Use the OTP below:</p>
                  <div style="background: #f4f4f4; border-radius: 8px; padding: 24px; text-align: center; margin: 24px 0;">
                    <span style="font-size: 40px; font-weight: bold; letter-spacing: 10px; color: #1a1a2e;">%s</span>
                  </div>
                  <p style="color: #555;">This OTP expires in <strong>10 minutes</strong>.</p>
                  <p style="color: #999; font-size: 13px;">If you did not request this, please ignore this email.</p>
                  <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;">
                  <p style="color: #999; font-size: 12px; text-align: center;">© 2024 Coursify. All rights reserved.</p>
                </div>
                """.formatted(otp);
    }
}