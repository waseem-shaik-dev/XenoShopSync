package com.example.XenoShopSync.dto;

public record ProductVariantDto(
        Long id,
        Long shopifyVariantId,
        Long shopifyProductId,
        String tenantId,
        String title,
        String option1,
        String option2,
        String option3,
        Double price,
        Integer position,
        Boolean taxable,
        String sku,
        Integer inventoryQuantity,
        Long inventoryItemId,
        Boolean requiresShipping
) {}
