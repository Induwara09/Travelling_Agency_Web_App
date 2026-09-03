package com.smartpos.service;

import com.smartpos.model.User;
import com.smartpos.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository users;
    public CurrentUserService(UserRepository users){ this.users=users; }
    public User get(){
        Authentication a= SecurityContextHolder.getContext().getAuthentication();
        if(a==null || !a.isAuthenticated()) throw new IllegalArgumentException("Not authenticated");
        return users.findByUsernameIgnoreCase(a.getName()).orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }
}
