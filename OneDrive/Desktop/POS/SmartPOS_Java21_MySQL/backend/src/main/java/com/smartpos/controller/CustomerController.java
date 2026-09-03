package com.smartpos.controller;
import com.smartpos.dto.CustomerDtos.*;
import com.smartpos.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/customers")
public class CustomerController {private final CustomerService s;public CustomerController(CustomerService s){this.s=s;}@GetMapping public List<CustomerView> list(){return s.list();}@PostMapping public CustomerView create(@Valid @RequestBody CustomerRequest r){return s.create(r);}@PutMapping("/{id}") public CustomerView update(@PathVariable Long id,@Valid @RequestBody CustomerRequest r){return s.update(id,r);}}
