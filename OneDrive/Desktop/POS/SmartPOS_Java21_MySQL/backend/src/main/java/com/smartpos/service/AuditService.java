package com.smartpos.service;

import com.smartpos.model.*;
import com.smartpos.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository repo;
    public AuditService(AuditLogRepository repo){ this.repo=repo; }
    public void log(User user,String action,String entityType,String entityId,String details){
        repo.save(AuditLog.builder().userId(user==null?null:user.getId()).username(user==null?"SYSTEM":user.getUsername())
                .role(user==null?"SYSTEM":user.getRole().name()).action(action).entityType(entityType).entityId(entityId).details(details).build());
    }
}
