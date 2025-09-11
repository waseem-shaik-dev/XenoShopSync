package com.example.XenoShopSync.dto;

public record AddressDto(
        Long id,
        String tenantId,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String city,
        String province,
        String country,
        String zip,
        String phone,
        boolean isDefault,
        String provinceCode,
        String countryCode,
        String countryName
) {}
