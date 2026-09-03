package com.smartpos.controller;

import com.smartpos.dto.UserDtos.*;
import com.smartpos.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/users") @PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService service; public UserController(UserService s){service=s;}
    @GetMapping public List<UserView> list(){return service.list();}
    @PostMapping public UserView create(@Valid @RequestBody UserCreateRequest r){return service.create(r);}
    @PutMapping("/{id}") public UserView update(@PathVariable Long id,@RequestBody UserUpdateRequest r){return service.update(id,r);}
}
