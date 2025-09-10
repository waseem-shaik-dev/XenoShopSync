package com.example.XenoShopSync.dto;

public record TenantRequestDto(
        String tenantId,
        String shopifyBaseUrl,
        String accessToken,
        String shopName
) {
}
