package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products", indexes = {@Index(name="idx_product_name", columnList="name"), @Index(name="idx_product_barcode", columnList="barcode")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, updatable = false, length = 36) private String externalId;
    @Column(unique = true, nullable = false, length = 40) private String itemCode;
    @Column(unique = true, length = 80) private String barcode;
    @Column(nullable = false, length = 150) private String name;
    @Column(length = 500) private String description;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="category_id") private Category category;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal sellingPrice = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal costPrice = BigDecimal.ZERO;
    @Column(nullable = false, length = 20) private String unit = "EA";
    @Column(nullable = false, precision = 14, scale = 3) private BigDecimal currentStock = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 3) private BigDecimal minStock = BigDecimal.ZERO;
    @Column(nullable = false) private boolean active = true;
    @Column(nullable = false) private boolean trackInventory = true;
    @Column(nullable = false) private boolean allowDiscount = true;
    @Column(length = 400) private String imageUrl;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    @PrePersist void prePersist(){ if(externalId==null) externalId= UUID.randomUUID().toString(); createdAt=LocalDateTime.now(); updatedAt=createdAt; }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
