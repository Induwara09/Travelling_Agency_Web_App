package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="stock_movements", indexes=@Index(name="idx_stock_product_created", columnList="product_id,createdAt"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private Product product;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private StockMovementType type;
    @Column(nullable=false, precision=14, scale=3) private BigDecimal quantityChange;
    @Column(nullable=false, precision=14, scale=3) private BigDecimal previousStock;
    @Column(nullable=false, precision=14, scale=3) private BigDecimal newStock;
    @Column(length=500) private String reason;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="performed_by") private User performedBy;
    @Column(length=40) private String referenceType;
    @Column(length=80) private String referenceId;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
