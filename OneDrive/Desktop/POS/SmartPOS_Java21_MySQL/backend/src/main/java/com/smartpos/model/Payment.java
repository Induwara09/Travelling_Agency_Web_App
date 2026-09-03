package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sale_id", nullable=false) private Sale sale;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private PaymentMethod method;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal amount;
    @Column(length=100) private String reference;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
