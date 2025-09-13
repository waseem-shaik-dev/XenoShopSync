package com.example.XenoShopSync.repository;



import com.example.XenoShopSync.entity.PasswordResetToken;
import com.example.XenoShopSync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);
    Optional<PasswordResetToken> findByUserAndOtp(User user, String otp);
}
