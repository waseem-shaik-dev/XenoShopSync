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
                @Index(name = "idx_order_shopify", columnList = "shopifyOrderId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // internal DB id

    // 🔹 Multi-tenant handling
    @Column(nullable = false)
    private String tenantId;

    // 🔹 Shopify identifiers
    private Long shopifyOrderId;
    private String adminGraphqlApiId;
    private Long orderNumber;
    private String name;   // e.g. #1002
    private String confirmationNumber;

    // 🔹 Status
    private String financialStatus;   // e.g. "paid"
    private String fulfillmentStatus; // e.g. "fulfilled", "null"
    private Boolean confirmed;

    // 🔹 Customer info
    private String email;
    private String phone;
    private String customerLocale;

    // 🔹 Monetary fields
    private String currency;
    private String presentmentCurrency;
    private Double subtotalPrice;
    private Double totalDiscounts;
    private Double totalPrice;
    private Double totalTax;
    private Double totalLineItemsPrice;
    private Double totalTipReceived;
    private Double totalOutstanding;

    // 🔹 Meta
    private String paymentGateway;  // simplified (manual, stripe, etc.)
    private String sourceName;      // e.g. shopify_draft_order
    private Boolean testOrder;
    private String tags;

    // 🔹 Dates
    private OffsetDateTime createdAt;
    private OffsetDateTime processedAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime cancelledAt;
    private OffsetDateTime closedAt;

    // 🔹 Relationships
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> lineItems = new ArrayList<>();

    // ✅ Using existing Address entity
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    public List<OrderLineItem> getLineItems() {
        if (lineItems == null) {
            lineItems = new ArrayList<>();
        }
        return lineItems;
    }


}
