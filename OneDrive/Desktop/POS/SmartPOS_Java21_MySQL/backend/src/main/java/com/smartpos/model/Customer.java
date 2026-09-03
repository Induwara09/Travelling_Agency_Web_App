package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String name;
    @Column(length=30) private String phone;
    @Column(length=160) private String email;
    @Column(length=300) private String address;
    @Builder.Default @Column(nullable=false, precision=12, scale=2) private BigDecimal loyaltyPoints = BigDecimal.ZERO;
}
