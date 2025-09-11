package com.example.XenoShopSync.dto;

public record OrderLineItemDto(
        Long id,
        Long shopifyLineItemId,
        Long shopifyProductId,
        Long shopifyVariantId,
        String title,
        Integer quantity,
        Double price,
        String sku,
        String tenantId
) {}
