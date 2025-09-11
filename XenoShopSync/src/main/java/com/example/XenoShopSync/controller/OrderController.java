package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.dto.OrderDto;
import com.example.XenoShopSync.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ Get all orders for a tenant
    @GetMapping("/{tenantId}")
    public ResponseEntity<List<OrderDto>> getAllOrders(@PathVariable String tenantId) {
        return ResponseEntity.ok(orderService.getAllOrders(tenantId));
    }

    // ✅ Get order by database ID
    @GetMapping("/id/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get order by Shopify order ID
    @GetMapping("/{tenantId}/shopify/{shopifyOrderId}")
    public ResponseEntity<OrderDto> getOrderByShopifyId(@PathVariable String tenantId,
                                                        @PathVariable Long shopifyOrderId) {
        return orderService.getOrderByShopifyId(tenantId, shopifyOrderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get orders by email
    @GetMapping("/{tenantId}/email/{email}")
    public ResponseEntity<List<OrderDto>> getOrdersByEmail(@PathVariable String tenantId,
                                                           @PathVariable String email) {
        return ResponseEntity.ok(orderService.getOrdersByEmail(tenantId, email));
    }

    // ✅ Get orders created after a date
    @GetMapping("/{tenantId}/created-after")
    public ResponseEntity<List<OrderDto>> getOrdersCreatedAfter(@PathVariable String tenantId,
                                                                @RequestParam String dateTime) {
        return ResponseEntity.ok(orderService.getOrdersCreatedAfter(tenantId, dateTime));
    }

    // ✅ Get orders updated after a date
    @GetMapping("/{tenantId}/updated-after")
    public ResponseEntity<List<OrderDto>> getOrdersUpdatedAfter(@PathVariable String tenantId,
                                                                @RequestParam String dateTime) {
        return ResponseEntity.ok(orderService.getOrdersUpdatedAfter(tenantId, dateTime));
    }

    // ✅ Count orders for a tenant
    @GetMapping("/{tenantId}/count")
    public ResponseEntity<Long> countOrders(@PathVariable String tenantId) {
        return ResponseEntity.ok(orderService.countOrdersByTenant(tenantId));
    }
}
