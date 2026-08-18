package com.example.travel.dto.auth;

import com.example.travel.enums.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String name,
        String email,
        Role role
) {
}
