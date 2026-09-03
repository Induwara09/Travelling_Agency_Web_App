package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="audit_logs", indexes=@Index(name="idx_audit_created", columnList="createdAt"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long userId;
    @Column(length=80) private String username;
    @Column(length=20) private String role;
    @Column(nullable=false, length=60) private String action;
    @Column(length=60) private String entityType;
    @Column(length=80) private String entityId;
    @Column(length=2000) private String details;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
