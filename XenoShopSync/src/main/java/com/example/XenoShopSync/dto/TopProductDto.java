package com.example.XenoShopSync.dto;

public record TopProductDto(
        Long productId,
        String title,
        double unitPrice,
        String imageSrc,
        int stock,
        int quantitySold,
        double revenue
) {}
