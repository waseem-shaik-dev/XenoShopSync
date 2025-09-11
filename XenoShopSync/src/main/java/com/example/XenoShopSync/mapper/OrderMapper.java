package com.example.XenoShopSync.mapper;

import com.example.XenoShopSync.dto.OrderDto;
import com.example.XenoShopSync.dto.OrderLineItemDto;
import com.example.XenoShopSync.entity.Order;
import com.example.XenoShopSync.entity.OrderLineItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        if (order == null) return null;

        List<OrderLineItemDto> lineItemDtos = order.getLineItems() != null
                ? order.getLineItems().stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return new OrderDto(
                order.getId(),
                order.getShopifyOrderId(),
                order.getTenantId(),
                order.getEmail(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getTotalPrice(),
                order.getCurrency(),
                order.getTotalItems(),
                lineItemDtos
        );
    }

    public static OrderLineItemDto toDto(OrderLineItem lineItem) {
        if (lineItem == null) return null;

        return new OrderLineItemDto(
                lineItem.getId(),
                lineItem.getShopifyLineItemId(),
                lineItem.getShopifyProductId(),
                lineItem.getShopifyVariantId(),
                lineItem.getTitle(),
                lineItem.getQuantity(),
                lineItem.getPrice(),
                lineItem.getSku(),
                lineItem.getTenantId()
        );
    }
}
