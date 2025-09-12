package com.example.XenoShopSync.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String tenantId,

        // Shopify identifiers
        Long shopifyOrderId,
        String adminGraphqlApiId,
        Long orderNumber,
        String name,
        String confirmationNumber,

        // Status
        String financialStatus,
        String fulfillmentStatus,
        Boolean confirmed,

        // Customer info
        String email,
        String phone,
        String customerLocale,

        // Monetary fields
        String currency,
        String presentmentCurrency,
        Double subtotalPrice,
        Double totalDiscounts,
        Double totalPrice,
        Double totalTax,
        Double totalLineItemsPrice,
        Double totalTipReceived,
        Double totalOutstanding,

        // Meta
        String paymentGateway,
        String sourceName,
        Boolean testOrder,
        String tags,

        // Dates
        OffsetDateTime createdAt,
        OffsetDateTime processedAt,
        OffsetDateTime updatedAt,
        OffsetDateTime cancelledAt,
        OffsetDateTime closedAt,

        // Relationships
        List<OrderLineItemDto> lineItems,
        AddressDto shippingAddress,
        AddressDto billingAddress
) {}
