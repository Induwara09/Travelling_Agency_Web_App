package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="suppliers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Supplier {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=150) private String name;
    @Column(length=120) private String contactPerson;
    @Column(length=40) private String phone;
    @Column(length=160) private String email;
    @Column(length=300) private String address;
    @Column(nullable=false) private boolean active = true;
}
