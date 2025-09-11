package com.example.XenoShopSync.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CustomerDto(
        Long id,
        Long shopifyId,
        String tenantId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String currency,
        Integer ordersCount,
        String state,
        String tags,
        Double totalSpent,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<AddressDto> addresses
) {}
