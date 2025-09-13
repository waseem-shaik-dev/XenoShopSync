package com.example.XenoShopSync.dto;

import com.example.XenoShopSync.enums.Role;

public record UserRegistrationDto(
        String email,
        String password,
        TenantRequestDto tenant  // Required only if role = TENANT
) {
}
