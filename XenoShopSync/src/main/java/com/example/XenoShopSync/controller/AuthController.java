package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.authService.AuthService;
import com.example.XenoShopSync.dto.OtpVerificationDto;
import com.example.XenoShopSync.dto.UserLoginDto;
import com.example.XenoShopSync.dto.UserRegistrationDto;
import com.example.XenoShopSync.entity.User;
import com.example.XenoShopSync.enums.Role;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Tenant registration (public)
     */
    @PostMapping("/register/tenant")
    public ResponseEntity<String> registerTenant(@RequestBody @Valid UserRegistrationDto dto) throws MessagingException {
        return ResponseEntity.ok(authService.registerTenant(dto));
    }

    /**
     * Admin registration (restricted)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<User> registerAdmin(@RequestBody @Valid UserRegistrationDto dto) {

        return ResponseEntity.ok(authService.registerAdmin(dto));
    }

    /**
     * Login (public)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthService.LoginResponse> login(
            @Valid @RequestBody UserLoginDto dto,
            HttpServletResponse response) {

        return ResponseEntity.ok(authService.login(dto, response));
    }


    // Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody OtpVerificationDto dto) {
        authService.verifyOtp(dto);
        return "Registration successful! Your account is now active.";
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email) throws MessagingException {
        authService.resendOtp(email);
        return "OTP resent successfully.";
    }
}
