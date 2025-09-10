package com.example.XenoShopSync.mapper;

import com.example.XenoShopSync.dto.TenantRequestDto;
import com.example.XenoShopSync.dto.TenantResponseDto;
import com.example.XenoShopSync.entity.Tenant;

public class TenantMapper {

    public static Tenant toEntity(TenantRequestDto dto) {
        return Tenant.builder()
                .tenantId(dto.tenantId())
                .shopifyBaseUrl(dto.shopifyBaseUrl())
                .accessToken(dto.accessToken())
                .shopName(dto.shopName())
                .build();
    }

    public static TenantResponseDto toResponseDto(Tenant tenant) {
        return new TenantResponseDto(
                tenant.getId(),
                tenant.getTenantId(),
                tenant.getShopifyBaseUrl(),
                tenant.getShopName()
        );
    }
}
