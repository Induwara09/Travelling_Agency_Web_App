package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false, length = 30) private String employeeId;
    @Column(nullable = false, length = 120) private String name;
    @Column(unique = true, nullable = false, length = 60) private String username;
    @Column(nullable = false, length = 120) private String passwordHash;
    @Column(length = 120) private String pinHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private RoleName role;
    @Column(nullable = false) private boolean active = true;
    private LocalDateTime lastLoginAt;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;

    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
