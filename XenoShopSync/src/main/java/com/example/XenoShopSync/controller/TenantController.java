package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.dto.TenantRequestDto;
import com.example.XenoShopSync.dto.TenantResponseDto;
import com.example.XenoShopSync.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TenantResponseDto>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

}
