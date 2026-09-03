package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="backups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BackupRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=500) private String filePath;
    @Column(nullable=false, length=30) private String status;
    private Long sizeBytes;
    @Column(length=1500) private String message;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
