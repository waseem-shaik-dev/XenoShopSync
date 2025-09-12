package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.dto.OrderDto;
import com.example.XenoShopSync.mapper.OrderMapper;
import com.example.XenoShopSync.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ------------------------
    // 📦 Standard CRUD Endpoints
    // ------------------------

    @GetMapping("/{tenantId}/all")
    public List<OrderDto> getAllOrders(@PathVariable String tenantId) {
        return OrderMapper.toDtoList(orderService.getAllOrders(tenantId));
    }

    @GetMapping("/{tenantId}/paged")
    public Page<OrderDto> getOrdersPaged(@PathVariable String tenantId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return orderService.getOrdersPaged(tenantId, PageRequest.of(page, size))
                .map(OrderMapper::toDto);
    }

    @GetMapping("/{tenantId}/id/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @GetMapping("/{tenantId}/shopify/{shopifyOrderId}")
    public OrderDto getOrderByShopifyId(@PathVariable String tenantId,
                                        @PathVariable Long shopifyOrderId) {
        return orderService.getOrderByShopifyId(tenantId, shopifyOrderId)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @GetMapping("/{tenantId}/recent")
    public List<OrderDto> getRecentOrders(@PathVariable String tenantId,
                                          @RequestParam(defaultValue = "5") int limit) {
        return OrderMapper.toDtoList(orderService.getRecentOrders(tenantId, limit));
    }

    // ------------------------
    // 📊 Analytics Endpoints
    // ------------------------

    @GetMapping("/{tenantId}/analytics/total-orders")
    public long getTotalOrders(@PathVariable String tenantId) {
        return orderService.getTotalOrders(tenantId);
    }

    @GetMapping("/{tenantId}/analytics/total-revenue")
    public double getTotalRevenue(@PathVariable String tenantId) {
        return orderService.getTotalRevenue(tenantId);
    }

    @GetMapping("/{tenantId}/analytics/average-order-value")
    public double getAverageOrderValue(@PathVariable String tenantId) {
        return orderService.getAverageOrderValue(tenantId);
    }

    @GetMapping("/{tenantId}/analytics/orders-by-date")
    public List<OrderDto> getOrdersByDateRange(@PathVariable String tenantId,
                                               @RequestParam String start,
                                               @RequestParam String end) {
        OffsetDateTime startDate = OffsetDateTime.parse(start);
        OffsetDateTime endDate = OffsetDateTime.parse(end);
        return OrderMapper.toDtoList(orderService.getOrdersByDateRange(tenantId, startDate, endDate));
    }

    @GetMapping("/{tenantId}/analytics/orders-trend")
    public List<Object[]> getOrdersGroupedByDate(@PathVariable String tenantId) {
        return orderService.getOrdersGroupedByDate(tenantId);
    }

    @GetMapping("/{tenantId}/analytics/revenue-trend")
    public List<Object[]> getRevenueTrend(@PathVariable String tenantId,
                                          @RequestParam String start,
                                          @RequestParam String end) {
        OffsetDateTime startDate = OffsetDateTime.parse(start);
        OffsetDateTime endDate = OffsetDateTime.parse(end);
        return orderService.getRevenueTrend(tenantId, startDate, endDate);
    }

    @GetMapping("/{tenantId}/analytics/top-customers")
    public List<Object[]> getTopCustomersBySpend(@PathVariable String tenantId,
                                                 @RequestParam(defaultValue = "5") int limit) {
        return orderService.getTopCustomersBySpend(tenantId, limit);
    }

    @GetMapping("/{tenantId}/analytics/by-financial-status")
    public Map<String, Long> getOrdersByFinancialStatus(@PathVariable String tenantId) {
        return orderService.getOrdersByFinancialStatus(tenantId);
    }

    @GetMapping("/{tenantId}/analytics/by-fulfillment-status")
    public Map<String, Long> getOrdersByFulfillmentStatus(@PathVariable String tenantId) {
        return orderService.getOrdersByFulfillmentStatus(tenantId);
    }
}
