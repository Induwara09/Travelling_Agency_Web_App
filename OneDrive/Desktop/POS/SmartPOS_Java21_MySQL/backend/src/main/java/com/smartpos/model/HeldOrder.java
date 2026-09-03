package com.smartpos.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="held_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HeldOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String name;
 @Lob @Column(nullable=false,columnDefinition="LONGTEXT") private String payload;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by",nullable=false) private User createdBy;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();}
}
