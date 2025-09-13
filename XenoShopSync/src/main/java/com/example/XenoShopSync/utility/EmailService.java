package com.example.XenoShopSync.utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    // Common send method
    private void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = enable HTML

        mailSender.send(message);
    }

    // OTP Email
    public void sendPasswordResetOtpEmail(String to, String otp) throws MessagingException {
        String subject = "🔐 Password Reset OTP";
        String body = "<h2>Your OTP Code</h2>" +
                "<p>Use the following OTP to reset your XenoShopSync account password:</p>" +
                "<h1 style='color:blue;'>" + otp + "</h1>" +
                "<p>This OTP will expire in 5 minutes.</p>" +
                "<br><p>If you didn’t request this, please ignore this email.</p>" +
                "<br><p>Team SalaryGen</p>";
        sendHtmlMail(to, subject, body);
    }



    // OTP Email for Registration
    public void sendRegistrationOtpEmail(String to, String otp) throws MessagingException {
        String subject = "✅ Complete Your Registration - OTP Verification";
        String body = "<h2>Welcome to <b>XenoShopSync</b>!</h2>" +
                "<p>Use the following OTP to verify your email and complete registration:</p>" +
                "<h1 style='color:green;'>" + otp + "</h1>" +
                "<p>This OTP will expire in 5 minutes.</p>" +
                "<br><p>If you didn’t request this registration, please ignore this email.</p>" +
                "<br><p>Cheers,<br>Team XenoShopSync</p>";
        sendHtmlMail(to, subject, body);
    }

}
