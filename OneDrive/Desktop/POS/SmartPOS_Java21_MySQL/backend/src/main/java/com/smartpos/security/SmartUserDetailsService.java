package com.smartpos.security;

import com.smartpos.model.User;
import com.smartpos.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class SmartUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public SmartUserDetailsService(UserRepository users){ this.users=users; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u=users.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(u.getUsername()).password(u.getPasswordHash())
                .disabled(!u.isActive()).roles(u.getRole().name()).build();
    }
}
