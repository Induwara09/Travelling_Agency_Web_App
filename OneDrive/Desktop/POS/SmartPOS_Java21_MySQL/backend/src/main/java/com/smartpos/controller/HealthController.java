package com.smartpos.controller;

import com.smartpos.config.AppProperties;
import com.smartpos.service.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {
    private final SyncService sync;private final EmailService email;private final AppProperties props;
    public HealthController(SyncService s,EmailService e,AppProperties p){sync=s;email=e;props=p;}
    @GetMapping("/api/health") public Map<String,Object> health(){return Map.of("status","UP","time",LocalDateTime.now(),"business",props.getBusiness().getName(),"syncEnabled",props.getSync().isEnabled(),"pendingSync",sync.pending(),"pendingEmail",email.pendingCount());}
    @GetMapping("/api/settings/public") public Map<String,Object> settings(){return Map.of("businessName",props.getBusiness().getName(),"address",props.getBusiness().getAddress(),"phone",props.getBusiness().getPhone(),"email",props.getBusiness().getEmail(),"currency",props.getBusiness().getCurrency(),"serviceChargePercent",props.getBusiness().getServiceChargePercent(),"taxPercent",props.getBusiness().getTaxPercent());}
}
