package com.smartpos.controller;

import com.smartpos.dto.AuthDtos.*;
import com.smartpos.service.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth; private final CurrentUserService current;
    public AuthController(AuthService auth,CurrentUserService current){this.auth=auth;this.current=current;}
    @PostMapping("/login") public LoginResponse login(@Valid @RequestBody LoginRequest r){return auth.login(r);}
    @GetMapping("/me") public UserView me(){return auth.view(current.get());}
    @PostMapping("/manager-authorize") public ManagerAuthorizeResponse authorize(@Valid @RequestBody ManagerAuthorizeRequest r){return auth.authorizeManager(r);}
}
