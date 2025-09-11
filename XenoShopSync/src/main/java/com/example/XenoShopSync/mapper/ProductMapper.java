package com.example.XenoShopSync.mapper;

import com.example.XenoShopSync.dto.ProductDto;
import com.example.XenoShopSync.dto.ProductVariantDto;
import com.example.XenoShopSync.entity.Product;
import com.example.XenoShopSync.entity.ProductVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
        if (product == null) return null;

        List<ProductVariantDto> variantDtos = product.getVariants() != null
                ? product.getVariants().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return new ProductDto(
                product.getId(),
                product.getShopifyId(),
                product.getTenantId(),
                product.getTitle(),
                product.getBodyHtml(),
                product.getVendor(),
                product.getProductType(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getPublishedAt(),
                product.getHandle(),
                product.getStatus(),
                product.getTags(),
                product.getImageSrc(),
                variantDtos
        );
    }

    public static ProductVariantDto toDto(ProductVariant variant) {
        if (variant == null) return null;

        return new ProductVariantDto(
                variant.getId(),
                variant.getShopifyVariantId(),
                variant.getShopifyProductId(),
                variant.getTenantId(),
                variant.getTitle(),
                variant.getOption1(),
                variant.getOption2(),
                variant.getOption3(),
                variant.getPrice(),
                variant.getPosition(),
                variant.getTaxable(),
                variant.getSku(),
                variant.getInventoryQuantity(),
                variant.getInventoryItemId(),
                variant.getRequiresShipping()
        );
    }
}
