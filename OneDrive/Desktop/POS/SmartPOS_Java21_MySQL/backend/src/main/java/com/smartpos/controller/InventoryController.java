package com.smartpos.controller;

import com.smartpos.dto.InventoryDtos.*;
import com.smartpos.dto.ProductDtos.ProductView;
import com.smartpos.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service; public InventoryController(InventoryService s){service=s;}
    @GetMapping("/low-stock") public List<ProductView> low(){return service.lowStock();}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @GetMapping("/movements") public List<StockMovementView> movements(){return service.movements();}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @PostMapping("/receive") public ProductView receive(@Valid @RequestBody StockChangeRequest r){return service.receive(r);}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @PostMapping("/adjust") public ProductView adjust(@Valid @RequestBody StockChangeRequest r){return service.adjust(r);}
}
