package com.example.XenoShopSync.dto;

public record TopCustomerDto(
        Long customerId,
        String name,
        String email,
        double totalSpent,
        int ordersCount
) {}
