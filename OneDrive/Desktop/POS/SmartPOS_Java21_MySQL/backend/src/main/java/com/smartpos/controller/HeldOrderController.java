package com.smartpos.controller;
import com.smartpos.model.*;
import com.smartpos.repository.HeldOrderRepository;
import com.smartpos.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RestController @RequestMapping("/api/held-orders")
public class HeldOrderController {
 private final HeldOrderRepository repo;private final CurrentUserService current;private final AuditService audit;
 public HeldOrderController(HeldOrderRepository r,CurrentUserService c,AuditService a){repo=r;current=c;audit=a;}
 public record Req(String name,String payload){}
 @Transactional(readOnly=true) @GetMapping public List<Map<String,Object>> list(){return repo.findTop100ByOrderByCreatedAtDesc().stream().map(h->{Map<String,Object>m=new LinkedHashMap<>();m.put("id",h.getId());m.put("name",h.getName());m.put("payload",h.getPayload());m.put("createdBy",h.getCreatedBy().getName());m.put("createdAt",h.getCreatedAt());return m;}).toList();}
 @PostMapping public Map<String,Object> save(@RequestBody Req r){if(r.payload()==null||r.payload().isBlank())throw new IllegalArgumentException("Order payload required");HeldOrder h=repo.save(HeldOrder.builder().name(r.name()==null||r.name().isBlank()?"Held Order":r.name()).payload(r.payload()).createdBy(current.get()).build());audit.log(current.get(),"ORDER_HELD","HELD_ORDER",h.getId().toString(),h.getName());return Map.of("id",h.getId(),"name",h.getName());}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){HeldOrder h=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Held order not found"));repo.delete(h);audit.log(current.get(),"HELD_ORDER_RESUMED_OR_REMOVED","HELD_ORDER",id.toString(),h.getName());}
}
