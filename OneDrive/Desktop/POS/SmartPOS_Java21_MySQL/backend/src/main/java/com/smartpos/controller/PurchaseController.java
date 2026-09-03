package com.smartpos.controller;

import com.smartpos.dto.PurchaseDtos.*;
import com.smartpos.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class PurchaseController {
    private final PurchaseService service; public PurchaseController(PurchaseService s){service=s;}
    @GetMapping("/suppliers") public List<SupplierView> suppliers(){return service.suppliers();}
    @PostMapping("/suppliers") public SupplierView addSupplier(@Valid @RequestBody SupplierRequest r){return service.addSupplier(r);}
    @GetMapping("/purchases") public List<PurchaseView> purchases(){return service.recent();}
    @PostMapping("/purchases") public PurchaseView create(@Valid @RequestBody PurchaseRequest r){return service.create(r);}
}
