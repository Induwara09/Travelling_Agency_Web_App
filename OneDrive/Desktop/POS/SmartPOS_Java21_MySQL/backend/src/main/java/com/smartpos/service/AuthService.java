package com.smartpos.service;

import com.smartpos.dto.AuthDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.UserRepository;
import com.smartpos.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AuthenticationManager authManager; private final UserRepository users; private final JwtService jwt; private final PasswordEncoder encoder; private final AuditService audit;
    public AuthService(AuthenticationManager authManager,UserRepository users,JwtService jwt,PasswordEncoder encoder,AuditService audit){ this.authManager=authManager;this.users=users;this.jwt=jwt;this.encoder=encoder;this.audit=audit; }
    public LoginResponse login(LoginRequest r){
        authManager.authenticate(new UsernamePasswordAuthenticationToken(r.username(),r.password()));
        User u=users.findByUsernameIgnoreCase(r.username()).orElseThrow(); u.setLastLoginAt(LocalDateTime.now()); users.save(u); audit.log(u,"LOGIN","USER",u.getId().toString(),"Successful login");
        return new LoginResponse(jwt.generate(u.getUsername(),u.getRole().name()),view(u));
    }
    public ManagerAuthorizeResponse authorizeManager(ManagerAuthorizeRequest r){
        User u=users.findByUsernameIgnoreCase(r.username()).orElseThrow(() -> new IllegalArgumentException("Manager account not found"));
        boolean ok=u.isActive() && (u.getRole()==RoleName.MANAGER || u.getRole()==RoleName.ADMIN) && encoder.matches(r.password(),u.getPasswordHash());
        if(!ok) throw new IllegalArgumentException("Manager authorization failed");
        return new ManagerAuthorizeResponse(true,u.getId(),u.getName(),u.getRole());
    }
    public UserView view(User u){ return new UserView(u.getId(),u.getEmployeeId(),u.getName(),u.getUsername(),u.getRole(),u.isActive()); }
}
