package com.smartpos.controller;
import com.smartpos.model.BackupRecord;
import com.smartpos.service.BackupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/backups") @PreAuthorize("hasRole('ADMIN')")
public class BackupController {private final BackupService service;public BackupController(BackupService s){service=s;}@GetMapping public List<BackupRecord> list(){return service.recent();}@PostMapping public BackupRecord create(){return service.create();}}
