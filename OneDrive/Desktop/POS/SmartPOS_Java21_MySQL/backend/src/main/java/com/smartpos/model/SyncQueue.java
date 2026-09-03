package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="sync_queue", indexes=@Index(name="idx_sync_status", columnList="status"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SyncQueue {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=50) private String entityType;
    @Column(nullable=false, length=80) private String entityId;
    @Column(nullable=false, length=50) private String operationType;
    @Lob @Column(columnDefinition="LONGTEXT") private String payload;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private QueueStatus status = QueueStatus.PENDING;
    @Column(nullable=false) private int retryCount = 0;
    @Column(length=1500) private String lastError;
    private LocalDateTime lastAttemptAt;
    private LocalDateTime syncedAt;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
