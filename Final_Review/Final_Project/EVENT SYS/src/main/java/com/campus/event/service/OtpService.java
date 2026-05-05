package com.campus.event.service;

import com.campus.event.entity.OtpRecord;
import com.campus.event.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final int OTP_EXPIRY_MINUTES = 5;

    /**
     * Generate and send OTP to the given email
     */
    public void generateAndSendOtp(String email) {
        String otp = generateOtp();

        OtpRecord otpRecord = new OtpRecord();
        otpRecord.setEmail(email);
        otpRecord.setOtpCode(otp);
        otpRecord.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpRecord.setVerified(false);
        otpRecord.setCreatedAt(LocalDateTime.now());

        otpRepository.save(otpRecord);

        sendOtpEmail(email, otp);
        log.info("OTP generated and sent to: {}", email);
    }

    /**
     * Verify the OTP entered by user
     */
    public boolean verifyOtp(String email, String otpCode) {
        Optional<OtpRecord> otpOpt = otpRepository.findValidOtp(email, LocalDateTime.now());

        if (otpOpt.isEmpty()) {
            log.warn("No valid OTP found for email: {}", email);
            return false;
        }

        OtpRecord otpRecord = otpOpt.get();

        if (otpRecord.isExpired()) {
            log.warn("OTP expired for email: {}", email);
            return false;
        }

        if (!otpRecord.getOtpCode().equals(otpCode)) {
            log.warn("Invalid OTP entered for email: {}", email);
            return false;
        }

        // Mark OTP as verified
        otpRecord.setVerified(true);
        otpRepository.save(otpRecord);
        log.info("OTP verified successfully for email: {}", email);
        return true;
    }

    /**
     * Check if email has a recently verified OTP
     */
    public boolean isEmailVerified(String email) {
        Optional<OtpRecord> otpOpt = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        return otpOpt.map(OtpRecord::isVerified).orElse(false);
    }

    /**
     * Generate random 6-digit OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Send OTP via email with HTML template
     */
    private void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, "Smart Campus Events");
            helper.setTo(toEmail);
            helper.setSubject("\uD83C\uDF93 Smart Campus Events - Your OTP Code");
            helper.setText(buildEmailTemplate(otp), true);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}. Error: {}", toEmail, e.getMessage());
            log.warn("DEV MODE - OTP for {}: {}", toEmail, otp);
        } catch (Exception e) {
            log.error("Unexpected error sending email: {}", e.getMessage());
            log.warn("DEV MODE - OTP for {}: {}", toEmail, otp);
        }
    }

    /**
     * HTML email template for OTP
     */
    private String buildEmailTemplate(String otp) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4ff; margin: 0; padding: 20px; }
                        .container { max-width: 500px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #667eea, #764ba2); padding: 30px; text-align: center; color: white; }
                        .header h1 { margin: 0; font-size: 24px; font-weight: 700; }
                        .header p { margin: 8px 0 0; opacity: 0.9; font-size: 14px; }
                        .body { padding: 30px; text-align: center; }
                        .otp-box { background: linear-gradient(135deg, #667eea22, #764ba222); border: 2px dashed #667eea; border-radius: 12px; padding: 25px; margin: 20px 0; }
                        .otp-code { font-size: 48px; font-weight: 800; letter-spacing: 10px; color: #667eea; font-family: monospace; }
                        .timer { background: #fff3cd; border-radius: 8px; padding: 10px; margin: 15px 0; color: #856404; font-size: 14px; }
                        .footer { background: #f8f9ff; padding: 20px; text-align: center; color: #666; font-size: 12px; border-top: 1px solid #e5e7eb; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎓 Smart Campus Events</h1>
                            <p>Email Verification</p>
                        </div>
                        <div class="body">
                            <p style="color: #374151; font-size: 16px;">You requested to register for a campus event. Use the OTP below to verify your email address.</p>
                            <div class="otp-box">
                                <div class="otp-code">%s</div>
                            </div>
                            <div class="timer">⏰ This OTP expires in <strong>5 minutes</strong></div>
                            <p style="color: #6b7280; font-size: 14px;">If you didn't request this, please ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>© 2024 Smart Campus Event Management System</p>
                            <p>Do not share this OTP with anyone.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(otp);
    }

    /**
     * Scheduled task to clean up expired OTPs every 30 minutes
     */
    @Scheduled(fixedRate = 1800000)
    public void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        log.debug("Cleaned up expired OTP records");
    }
}
