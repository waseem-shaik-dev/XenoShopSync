package com.example.XenoShopSync.repository;

import com.example.XenoShopSync.entity.RegistrationToken;
import com.example.XenoShopSync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
    Optional<RegistrationToken> findByUser(User user);
    Optional<RegistrationToken> findByUserAndOtp(User user, String otp);
}
