package com.smartpos.config;

import com.smartpos.security.JwtAuthFilter;
import com.smartpos.security.SmartUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean AuthenticationProvider authenticationProvider(SmartUserDetailsService uds, PasswordEncoder pe){
        DaoAuthenticationProvider p=new DaoAuthenticationProvider(); p.setUserDetailsService(uds); p.setPasswordEncoder(pe); return p;
    }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception { return cfg.getAuthenticationManager(); }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
        return http.csrf(csrf->csrf.disable()).cors(c->c.configurationSource(corsSource()))
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/api/health","/api/devices/heartbeat","/actuator/health","/","/index.html","/assets/**","/*.js","/*.css","/*.ico").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean CorsConfigurationSource corsSource(){
        CorsConfiguration c=new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of("http://localhost:*","http://127.0.0.1:*"));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*")); c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**",c); return s;
    }
}
