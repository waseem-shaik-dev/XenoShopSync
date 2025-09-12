package com.example.XenoShopSync.dto;

public record OrderLineItemDto(
        Long id,
        String tenantId,

        Long shopifyLineItemId,
        String adminGraphqlApiId,

        String title,
        Long productId,
        Long variantId,
        String vendor,

        Double price,
        Integer quantity,
        Boolean giftCard,
        Boolean requiresShipping,
        Boolean taxable
) {}
