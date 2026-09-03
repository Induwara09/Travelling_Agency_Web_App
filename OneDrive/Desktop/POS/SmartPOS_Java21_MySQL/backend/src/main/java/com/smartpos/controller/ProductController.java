package com.smartpos.controller;

import com.smartpos.dto.ProductDtos.*;
import com.smartpos.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api")
public class ProductController {
    private final ProductService service; public ProductController(ProductService s){service=s;}
    @GetMapping("/pos/menu") public List<ProductView> menu(@RequestParam(required=false) String q,@RequestParam(required=false) Long categoryId){return service.list(q,categoryId);}
    @GetMapping("/categories") public List<CategoryView> categories(){return service.categories();}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @PostMapping("/categories") public CategoryView addCategory(@Valid @RequestBody CategoryRequest r){return service.createCategory(r);}
    @GetMapping("/products") public List<ProductView> products(@RequestParam(required=false) String q,@RequestParam(required=false) Long categoryId){return service.list(q,categoryId);}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @PostMapping("/products") public ProductView create(@Valid @RequestBody ProductRequest r){return service.create(r);}
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')") @PutMapping("/products/{id}") public ProductView update(@PathVariable Long id,@Valid @RequestBody ProductRequest r){return service.update(id,r);}
}
