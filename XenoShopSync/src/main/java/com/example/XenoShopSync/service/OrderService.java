package com.example.XenoShopSync.service;

import com.example.XenoShopSync.dto.OrderDto;
import com.example.XenoShopSync.entity.Order;
import com.example.XenoShopSync.entity.OrderLineItem;
import com.example.XenoShopSync.entity.Tenant;
import com.example.XenoShopSync.mapper.OrderMapper;
import com.example.XenoShopSync.repository.OrderRepository;
import com.example.XenoShopSync.repository.TenantRepository;
import com.example.XenoShopSync.utility.ShopifyClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TenantRepository tenantRepository;
    private final ShopifyClient shopifyClient;

    public OrderService(OrderRepository orderRepository,
                        TenantRepository tenantRepository,
                        ShopifyClient shopifyClient) {
        this.orderRepository = orderRepository;
        this.tenantRepository = tenantRepository;
        this.shopifyClient = shopifyClient;
    }


    public void scheduledSync() {
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            syncOrdersFromShopify(tenant);
        }
    }

    @Transactional
    public void syncOrdersFromShopify(Tenant tenant) {
        Map<String, Object> response = shopifyClient.get(tenant, "/orders.json?status=any");
        if (response == null || !response.containsKey("orders")) return;
        List<Map<String, Object>> orders = (List<Map<String, Object>>) response.get("orders");
        for (Map<String, Object> o : orders) {
            upsertOrder(o, tenant.getTenantId());
        }
    }

    private void upsertOrder(Map<String, Object> o, String tenantId) {
        Long shopifyOrderId = Long.valueOf(o.get("id").toString());
        Optional<Order> existingOpt = orderRepository.findByTenantIdAndShopifyOrderId(tenantId, shopifyOrderId);

        Order order = existingOpt.orElseGet(() -> Order.builder()
                .shopifyOrderId(shopifyOrderId)
                .tenantId(tenantId)
                .totalPrice(0.0)
                .totalItems(0)
                .build());

        order.setEmail((String) o.get("email"));
        order.setCreatedAt(parseDate(o.get("created_at")));
        order.setUpdatedAt(parseDate(o.get("updated_at")));
        order.setTotalPrice(toDouble(o.get("total_price")));
        order.setCurrency((String) o.get("currency"));

        List<Map<String, Object>> lineItems = (List<Map<String, Object>>) o.get("line_items");
        if (lineItems != null) {
            order.getLineItems().clear();
            int totalItems = 0;
            for (Map<String, Object> li : lineItems) {
                Integer quantity = toInt(li.get("quantity"));
                totalItems += quantity;
                OrderLineItem lineItem = OrderLineItem.builder()
                        .shopifyLineItemId(li.get("id") != null ? Long.valueOf(li.get("id").toString()) : null)
                        .shopifyProductId(li.get("product_id") != null ? Long.valueOf(li.get("product_id").toString()) : null)
                        .shopifyVariantId(li.get("variant_id") != null ? Long.valueOf(li.get("variant_id").toString()) : null)
                        .title((String) li.get("title"))
                        .quantity(quantity)
                        .price(toDouble(li.get("price")))
                        .sku((String) li.get("sku"))
                        .tenantId(tenantId)
                        .build();

                order.addLineItem(lineItem);
            }
            order.setTotalItems(totalItems);
        }

        orderRepository.save(order);
    }

    private OffsetDateTime parseDate(Object o) {
        if (o == null) return null;
        try {
            return OffsetDateTime.parse(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Double toDouble(Object o) {
        if (o == null) return 0.0;
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer toInt(Object o) {
        if (o == null) return 0;
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }




    // ✅ Get all orders for a tenant
    public List<OrderDto> getAllOrders(String tenantId) {
        return orderRepository.findByTenantId(tenantId).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Get order by database ID
    public Optional<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toDto);
    }

    // ✅ Get order by Shopify order ID for a tenant
    public Optional<OrderDto> getOrderByShopifyId(String tenantId, Long shopifyOrderId) {
        return orderRepository.findByTenantIdAndShopifyOrderId(tenantId, shopifyOrderId)
                .map(OrderMapper::toDto);
    }

    // ✅ Get orders by customer email
    public List<OrderDto> getOrdersByEmail(String tenantId, String email) {
        return orderRepository.findByTenantIdAndEmail(tenantId, email).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Get orders created after a certain date
    public List<OrderDto> getOrdersCreatedAfter(String tenantId, String isoDateTime) {

        OffsetDateTime dateTime = OffsetDateTime.parse(isoDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return orderRepository.findByTenantIdAndCreatedAtAfter(tenantId, dateTime).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Get orders updated after a certain date
    public List<OrderDto> getOrdersUpdatedAfter(String tenantId, String isoDateTime) {
        OffsetDateTime dateTime = OffsetDateTime.parse(isoDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return orderRepository.findByTenantIdAndUpdatedAtAfter(tenantId, dateTime).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Count orders for a tenant
    public long countOrdersByTenant(String tenantId) {
        return orderRepository.countByTenantId(tenantId);
    }



}
