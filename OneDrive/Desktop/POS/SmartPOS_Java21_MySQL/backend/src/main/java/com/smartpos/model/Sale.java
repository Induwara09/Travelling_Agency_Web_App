package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="sales", indexes={@Index(name="idx_sale_invoice", columnList="invoiceNumber"), @Index(name="idx_sale_created", columnList="createdAt")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sale {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=50) private String invoiceNumber;
    @Column(nullable=false, unique=true, updatable=false, length=36) private String externalId;
    @Column(nullable=false, length=30) private String orderType = "TAKEAWAY";
    @Column(length=30) private String tableNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id") private Customer customer;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cashier_id", nullable=false) private User cashier;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal discount = BigDecimal.ZERO;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal serviceCharge = BigDecimal.ZERO;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal tax = BigDecimal.ZERO;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal total = BigDecimal.ZERO;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal amountPaid = BigDecimal.ZERO;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal balance = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private SaleStatus status = SaleStatus.COMPLETED;
    @Column(nullable=false) private boolean managerOverride = false;
    @Column(length=500) private String overrideReason;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="approved_by") private User approvedBy;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @OneToMany(mappedBy="sale", cascade=CascadeType.ALL, orphanRemoval=true) @Builder.Default private List<SaleItem> items = new ArrayList<>();
    @OneToMany(mappedBy="sale", cascade=CascadeType.ALL, orphanRemoval=true) @Builder.Default private List<Payment> payments = new ArrayList<>();

    @PrePersist void prePersist(){ if(externalId==null) externalId=UUID.randomUUID().toString(); if(createdAt==null) createdAt=LocalDateTime.now(); }
}
