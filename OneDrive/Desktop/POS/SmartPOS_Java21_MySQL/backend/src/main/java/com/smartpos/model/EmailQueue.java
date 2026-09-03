package com.smartpos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="email_queue", indexes=@Index(name="idx_email_status", columnList="status"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailQueue {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long saleId;
    @Column(nullable=false, length=200) private String recipient;
    @Column(nullable=false, length=250) private String subject;
    @Lob @Column(columnDefinition="TEXT") private String body;
    @Column(length=200) private String attachmentName;
    @Lob @Column(columnDefinition="LONGBLOB") private byte[] attachmentData;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private QueueStatus status = QueueStatus.PENDING;
    @Column(nullable=false) private int retryCount = 0;
    @Column(length=1500) private String lastError;
    private LocalDateTime lastAttemptAt;
    private LocalDateTime sentAt;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
