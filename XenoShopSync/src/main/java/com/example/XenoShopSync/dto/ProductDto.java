package com.example.XenoShopSync.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ProductDto(
        Long id,
        Long shopifyId,
        String tenantId,
        String title,
        String bodyHtml,
        String vendor,
        String productType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime publishedAt,
        String handle,
        String status,
        String tags,
        String imageSrc,
        List<ProductVariantDto> variants
) {}
