package com.smartpos.dto;

import com.smartpos.model.RoleName;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class UserDtos {
    public record UserCreateRequest(@NotBlank String employeeId, @NotBlank String name, @NotBlank String username,
                                    @NotBlank @Size(min=6) String password, String pin, @NotNull RoleName role) {}
    public record UserUpdateRequest(String name, String password, String pin, RoleName role, Boolean active) {}
    public record UserView(Long id, String employeeId, String name, String username, RoleName role, boolean active,
                           LocalDateTime lastLoginAt, LocalDateTime createdAt) {}
}
