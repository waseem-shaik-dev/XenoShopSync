package com.example.XenoShopSync.enums;

public enum UserStatus {
    PENDING,   // waiting for email OTP verification
    ACTIVE,    // verified and enabled
    SUSPENDED  // optional (if you want to block users later)
}
