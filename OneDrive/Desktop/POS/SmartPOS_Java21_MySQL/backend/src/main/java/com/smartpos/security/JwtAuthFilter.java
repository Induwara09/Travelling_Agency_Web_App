package com.smartpos.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final SmartUserDetailsService uds;
    public JwtAuthFilter(JwtService jwt, SmartUserDetailsService uds){ this.jwt=jwt; this.uds=uds; }
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h=req.getHeader("Authorization");
        if(h!=null && h.startsWith("Bearer ")){
            String token=h.substring(7);
            if(jwt.valid(token) && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails ud=uds.loadUserByUsername(jwt.username(token));
                var auth=new UsernamePasswordAuthenticationToken(ud,null,ud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req,res);
    }
}
