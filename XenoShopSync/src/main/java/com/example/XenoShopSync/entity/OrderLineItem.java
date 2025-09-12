package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_line_items",
        indexes = {
                @Index(name = "idx_lineitem_tenant", columnList = "tenantId"),
                @Index(name = "idx_lineitem_shopify", columnList = "shopifyLineItemId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;

    // 🔹 Shopify identifiers
    private Long shopifyLineItemId;
    private String adminGraphqlApiId;

    // 🔹 Product info
    private String title;
    private Long productId;
    private Long variantId;
    private String vendor;

    // 🔹 Pricing & qty
    private Double price;
    private Integer quantity;
    private Boolean giftCard;
    private Boolean requiresShipping;
    private Boolean taxable;

    // 🔹 Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
