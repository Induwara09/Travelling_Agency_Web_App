package com.smartpos.dto;

import com.smartpos.model.RoleName;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record UserView(Long id, String employeeId, String name, String username, RoleName role, boolean active) {}
    public record LoginResponse(String token, UserView user) {}
    public record ManagerAuthorizeRequest(@NotBlank String username, @NotBlank String password) {}
    public record ManagerAuthorizeResponse(boolean authorized, Long userId, String name, RoleName role) {}
}
