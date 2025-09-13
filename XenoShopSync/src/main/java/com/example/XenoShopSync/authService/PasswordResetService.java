package com.example.XenoShopSync.authService;



import com.example.XenoShopSync.entity.PasswordResetToken;
import com.example.XenoShopSync.entity.User;
import com.example.XenoShopSync.repository.PasswordResetTokenRepository;
import com.example.XenoShopSync.repository.UserRepository;
import com.example.XenoShopSync.utility.EmailService;
import com.example.XenoShopSync.utility.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Step 1: Request reset (generate OTP)
    public void requestPasswordReset(String email) throws MessagingException {


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String otp = OtpUtil.generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        PasswordResetToken token = tokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());
        token.setUser(user);
        token.setOtp(otp);
        token.setExpiry(expiry);

        tokenRepository.save(token);

        emailService.sendPasswordResetOtpEmail(user.getEmail(), otp);
    }



    // Step 2: Reset password
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken token = tokenRepository.findByUserAndOtp(user, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // delete used token
        tokenRepository.delete(token);
    }
}
