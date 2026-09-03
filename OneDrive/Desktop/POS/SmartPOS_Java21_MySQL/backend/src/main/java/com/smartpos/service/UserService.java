package com.smartpos.service;

import com.smartpos.dto.UserDtos.*;
import com.smartpos.model.*;
import com.smartpos.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {
    private final UserRepository users; private final PasswordEncoder encoder; private final CurrentUserService current; private final AuditService audit;
    public UserService(UserRepository users,PasswordEncoder encoder,CurrentUserService current,AuditService audit){this.users=users;this.encoder=encoder;this.current=current;this.audit=audit;}
    public List<UserView> list(){return users.findAll().stream().map(this::view).toList();}
    @Transactional public UserView create(UserCreateRequest r){
        if(users.existsByUsernameIgnoreCase(r.username())) throw new IllegalArgumentException("Username already exists");
        if(users.existsByEmployeeIdIgnoreCase(r.employeeId())) throw new IllegalArgumentException("Employee ID already exists");
        User u=users.save(User.builder().employeeId(r.employeeId()).name(r.name()).username(r.username()).passwordHash(encoder.encode(r.password())).pinHash(r.pin()==null||r.pin().isBlank()?null:encoder.encode(r.pin())).role(r.role()).active(true).build());
        audit.log(current.get(),"USER_CREATED","USER",u.getId().toString(),u.getUsername()+" role="+u.getRole()); return view(u);
    }
    @Transactional public UserView update(Long id,UserUpdateRequest r){
        User u=users.findById(id).orElseThrow(()->new IllegalArgumentException("User not found"));
        if(r.name()!=null&&!r.name().isBlank())u.setName(r.name()); if(r.password()!=null&&!r.password().isBlank())u.setPasswordHash(encoder.encode(r.password())); if(r.pin()!=null&&!r.pin().isBlank())u.setPinHash(encoder.encode(r.pin())); if(r.role()!=null)u.setRole(r.role()); if(r.active()!=null)u.setActive(r.active());users.save(u);audit.log(current.get(),"USER_UPDATED","USER",id.toString(),u.getUsername()+" role="+u.getRole()+" active="+u.isActive());return view(u);
    }
    private UserView view(User u){return new UserView(u.getId(),u.getEmployeeId(),u.getName(),u.getUsername(),u.getRole(),u.isActive(),u.getLastLoginAt(),u.getCreatedAt());}
}
