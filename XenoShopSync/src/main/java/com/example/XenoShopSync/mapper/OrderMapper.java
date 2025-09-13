package com.example.XenoShopSync.mapper;

import com.example.XenoShopSync.dto.*;
import com.example.XenoShopSync.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {
        // prevent instantiation
    }

    // ----------------------
    // Order -> OrderDto
    // ----------------------
    public static OrderDto toDto(Order order) {
        if (order == null) return null;

        return new OrderDto(
                order.getId(),
                order.getTenantId(),
                order.getShopifyOrderId(),
                order.getAdminGraphqlApiId(),
                order.getOrderNumber(),
                order.getName(),
                order.getConfirmationNumber(),

                order.getFinancialStatus(),
                order.getFulfillmentStatus(),
                order.getConfirmed(),

                order.getEmail(),
                order.getPhone(),
                order.getCustomerLocale(),
                order.getShopifyCustomerId(),

                order.getCurrency(),
                order.getPresentmentCurrency(),
                order.getSubtotalPrice(),
                order.getTotalDiscounts(),
                order.getTotalPrice(),
                order.getTotalTax(),
                order.getTotalLineItemsPrice(),
                order.getTotalTipReceived(),
                order.getTotalOutstanding(),

                order.getPaymentGateway(),
                order.getSourceName(),
                order.getTestOrder(),
                order.getTags(),

                order.getCreatedAt(),
                order.getProcessedAt(),
                order.getUpdatedAt(),
                order.getCancelledAt(),
                order.getClosedAt(),

                toLineItemDtoList(order.getLineItems()),
                CustomerMapper.toDto(order.getShippingAddress()),  // 🔁 reuse
                CustomerMapper.toDto(order.getBillingAddress())   // 🔁 reuse
        );
    }

    public static List<OrderDto> toDtoList(List<Order> orders) {
        return orders == null ? List.of() :
                orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    // ----------------------
    // OrderLineItem -> OrderLineItemDto
    // ----------------------
    private static List<OrderLineItemDto> toLineItemDtoList(List<OrderLineItem> lineItems) {
        if (lineItems == null) return List.of();
        return lineItems.stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    private static OrderLineItemDto toDto(OrderLineItem li) {
        if (li == null) return null;
        return new OrderLineItemDto(
                li.getId(),
                li.getTenantId(),
                li.getShopifyLineItemId(),
                li.getAdminGraphqlApiId(),
                li.getTitle(),
                li.getProductId(),
                li.getVariantId(),
                li.getVendor(),
                li.getPrice(),
                li.getQuantity(),
                li.getGiftCard(),
                li.getRequiresShipping(),
                li.getTaxable()
        );
    }
}
