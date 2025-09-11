package com.example.XenoShopSync.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long shopifyOrderId,
        String tenantId,
        String email,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Double totalPrice,
        String currency,
        Integer totalItems,
        List<OrderLineItemDto> lineItems
) {}
