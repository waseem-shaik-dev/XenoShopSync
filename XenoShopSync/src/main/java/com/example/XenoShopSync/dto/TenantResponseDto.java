package com.example.XenoShopSync.dto;

public record TenantResponseDto(
        Long id,
        String tenantId,
        String shopifyBaseUrl,
        String shopName
) {}
