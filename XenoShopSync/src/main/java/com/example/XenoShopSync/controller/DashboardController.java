package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.dto.DashboardResponse;
import com.example.XenoShopSync.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Example: /api/admin/{tenantId}/dashboard?from=2025-08-01&to=2025-08-31
     * If from/to not provided defaults to last 30 days (including today).
     */
    @GetMapping("/{tenantId}/dashboard")
    public DashboardResponse getDashboard(
            @PathVariable String tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return dashboardService.getDashboard(tenantId, from, to);
    }
}
