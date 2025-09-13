package com.example.XenoShopSync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerificationDto {
    private String email;
    private String otp;
}
