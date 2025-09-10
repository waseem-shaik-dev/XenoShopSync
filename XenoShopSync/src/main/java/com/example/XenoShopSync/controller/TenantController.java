package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.dto.TenantRequestDto;
import com.example.XenoShopSync.dto.TenantResponseDto;
import com.example.XenoShopSync.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponseDto> registerTenant(@RequestBody TenantRequestDto requestDto) {
        return ResponseEntity.ok(tenantService.registerTenant(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<TenantResponseDto>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponseDto> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @GetMapping("/by-tenant-id/{tenantId}")
    public ResponseEntity<TenantResponseDto> getTenantByTenantId(@PathVariable String tenantId) {
        return ResponseEntity.ok(tenantService.getTenantByTenantId(tenantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantResponseDto> updateTenant(
            @PathVariable Long id,
            @RequestBody TenantRequestDto requestDto) {
        return ResponseEntity.ok(tenantService.updateTenant(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-tenant-id/{tenantId}")
    public ResponseEntity<Void> deleteTenantByTenantId(@PathVariable String tenantId) {
        tenantService.deleteTenantByTenantId(tenantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.existsById(id));
    }

    @GetMapping("/exists/by-tenant-id/{tenantId}")
    public ResponseEntity<Boolean> existsByTenantId(@PathVariable String tenantId) {
        return ResponseEntity.ok(tenantService.existsByTenantId(tenantId));
    }
}
