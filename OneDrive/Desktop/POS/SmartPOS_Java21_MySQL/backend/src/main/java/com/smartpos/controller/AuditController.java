package com.smartpos.controller;
import com.smartpos.model.AuditLog;
import com.smartpos.repository.AuditLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/audit") @PreAuthorize("hasRole('ADMIN')")
public class AuditController { private final AuditLogRepository repo;public AuditController(AuditLogRepository r){repo=r;}@GetMapping public List<AuditLog> list(){return repo.findTop300ByOrderByCreatedAtDesc();}}
