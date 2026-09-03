package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="purchases")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Purchase {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id", nullable=false) private Supplier supplier;
    @Column(length=100) private String invoiceNumber;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal total = BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private User createdBy;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @OneToMany(mappedBy="purchase", cascade=CascadeType.ALL, orphanRemoval=true) @Builder.Default private List<PurchaseItem> items = new ArrayList<>();
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
