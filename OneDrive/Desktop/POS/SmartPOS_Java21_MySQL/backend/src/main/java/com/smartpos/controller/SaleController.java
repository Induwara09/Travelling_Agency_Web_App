package com.smartpos.controller;

import com.smartpos.dto.SaleDtos.*;
import com.smartpos.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/sales")
public class SaleController {
    private final SaleService service;public SaleController(SaleService s){service=s;}
    @PostMapping("/checkout") public SaleView checkout(@Valid @RequestBody CheckoutRequest r){return service.checkout(r);}
    @GetMapping public List<SaleView> recent(){return service.recent();}
    @GetMapping("/{id}") public SaleView get(@PathVariable Long id){return service.get(id);}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @PostMapping("/{id}/void") public SaleView voidSale(@PathVariable Long id,@RequestBody Map<String,String> body){return service.voidSale(id,body.getOrDefault("reason","Manager void"));}
}
