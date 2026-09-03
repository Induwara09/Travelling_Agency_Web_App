package com.smartpos.controller;
import com.smartpos.dto.RecipeDtos.*;
import com.smartpos.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/recipes") @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class RecipeController {private final RecipeService s;public RecipeController(RecipeService s){this.s=s;}@GetMapping("/{menuProductId}") public RecipeView get(@PathVariable Long menuProductId){return s.get(menuProductId);}@PutMapping public RecipeView save(@Valid @RequestBody RecipeRequest r){return s.save(r);}}
