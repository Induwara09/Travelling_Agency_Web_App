package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="cashier_shifts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CashierShift {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cashier_id", nullable=false) private User cashier;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal openingCash = BigDecimal.ZERO;
    @Column(precision=14, scale=2) private BigDecimal expectedCash;
    @Column(precision=14, scale=2) private BigDecimal actualCash;
    @Column(precision=14, scale=2) private BigDecimal difference;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private ShiftStatus status = ShiftStatus.OPEN;
    @Column(nullable=false) private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    @PrePersist void prePersist(){ if(openedAt==null) openedAt=LocalDateTime.now(); }
}
