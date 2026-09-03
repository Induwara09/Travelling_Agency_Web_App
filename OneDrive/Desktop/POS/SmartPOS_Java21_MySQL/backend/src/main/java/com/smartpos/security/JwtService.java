package com.smartpos.security;

import com.smartpos.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final AppProperties props;
    public JwtService(AppProperties props){ this.props=props; }
    private SecretKey key(){ return Keys.hmacShaKeyFor(props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8)); }
    public String generate(String username, String role){
        Instant now=Instant.now();
        return Jwts.builder().subject(username).claim("role", role).issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getJwt().getExpirationMinutes()*60)))
                .signWith(key()).compact();
    }
    public Claims parse(String token){ return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload(); }
    public String username(String token){ return parse(token).getSubject(); }
    public boolean valid(String token){ try { return parse(token).getExpiration().after(new Date()); } catch(Exception e){ return false; } }
}
