package com.example.XenoShopSync.mapper;


import com.example.XenoShopSync.dto.TenantResponseDto;
import com.example.XenoShopSync.entity.Tenant;

public class TenantMapper {

    public static TenantResponseDto toResponseDto(Tenant tenant) {
        return new TenantResponseDto(
                tenant.getId(),
                tenant.getTenantId(),
                tenant.getShopifyBaseUrl(),
                tenant.getShopName()
        );
    }
}
