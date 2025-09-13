package com.example.XenoShopSync.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        String tenantId,

        // Date ranges
        LocalDate from,
        LocalDate to,
        LocalDate previousFrom,
        LocalDate previousTo,

        // ---- KPI Cards ----
        double totalRevenue,
        double totalRevenueChangePercent,

        int totalOrders,
        double totalOrdersChangePercent,

        int totalCustomers,
        int newCustomers,
        double totalCustomersChangePercent,

        int totalProducts,

        // ---- Trends ----
        Map<LocalDate, Double> revenueTrend,
        Map<LocalDate, Long> ordersByDay,

        // ---- Insights ----
        List<TopProductDto> topProducts,
        List<TopCustomerDto> topCustomers
) {}
