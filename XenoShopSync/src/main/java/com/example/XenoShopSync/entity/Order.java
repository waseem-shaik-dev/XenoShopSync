package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_order_tenant", columnList = "tenantId"),
                @Index(name = "idx_order_shopifyid", columnList = "shopifyOrderId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shopifyOrderId;
    private String tenantId;

    private String email;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Double totalPrice;
    private String currency;
    private Integer totalItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderLineItem> lineItems = new ArrayList<>();

    public void addLineItem(OrderLineItem li) {
        li.setOrder(this);
        lineItems.add(li);
    }

    public void removeLineItem(OrderLineItem li) {
        lineItems.remove(li);
        li.setOrder(null);
    }
}
