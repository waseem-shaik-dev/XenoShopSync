package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.dto.CustomerDto;
import com.example.XenoShopSync.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ✅ Get all customers for a tenant
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<CustomerDto>> getAllCustomers(@PathVariable String tenantId) {
        return ResponseEntity.ok(customerService.getAllCustomers(tenantId));
    }

    // ✅ Get customer by database ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get customer by Shopify ID for a tenant
    @GetMapping("/tenant/{tenantId}/shopify/{shopifyId}")
    public ResponseEntity<CustomerDto> getCustomerByShopifyId(
            @PathVariable String tenantId,
            @PathVariable Long shopifyId) {
        return customerService.getCustomerByShopifyId(tenantId, shopifyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get customers by email for a tenant
    @GetMapping("/tenant/{tenantId}/email")
    public ResponseEntity<List<CustomerDto>> getCustomersByEmail(
            @PathVariable String tenantId,
            @RequestParam String email) {
        return ResponseEntity.ok(customerService.getCustomersByEmail(tenantId, email));
    }

    // ✅ Get customers by tag for a tenant
    @GetMapping("/tenant/{tenantId}/tag")
    public ResponseEntity<List<CustomerDto>> getCustomersByTag(
            @PathVariable String tenantId,
            @RequestParam String tag) {
        return ResponseEntity.ok(customerService.getCustomersByTag(tenantId, tag));
    }
}
