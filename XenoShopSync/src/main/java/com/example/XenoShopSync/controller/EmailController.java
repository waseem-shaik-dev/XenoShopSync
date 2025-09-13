package com.example.XenoShopSync.controller;


import com.example.XenoShopSync.authService.PasswordResetService;
import com.example.XenoShopSync.dto.PasswordResetDto;
import com.example.XenoShopSync.repository.UserRepository;
import com.example.XenoShopSync.utility.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/staff/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;






    // 1. Request password reset (send OTP)
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) throws MessagingException {
        passwordResetService.requestPasswordReset(email);
        return "OTP sent ";
    }

    // 2. Reset password using OTP with DTO
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordResetDto dto) {
        passwordResetService.resetPassword(dto.getEmail(), dto.getOtp(), dto.getNewPassword());
        return "Password reset successful!";
    }

}
