package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_line_items",
        indexes = {
                @Index(name = "idx_lineitem_tenant", columnList = "tenantId")
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

    private Long shopifyLineItemId;
    private Long shopifyProductId;
    private Long shopifyVariantId;
    private String title;
    private Integer quantity;
    private Double price;
    private String sku;
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
