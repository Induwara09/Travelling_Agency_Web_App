package com.smartpos.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="devices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Device {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=80) private String terminalId;
 @Column(nullable=false,length=150) private String deviceName;
 private Double cpuPercent; private Double ramPercent; private Double diskPercent;
 private Boolean posRunning; private Boolean databaseRunning;
 @Column(length=40) private String windowsVersion; @Column(length=40) private String appVersion;
 @Column(nullable=false) private LocalDateTime lastSeen;
}
