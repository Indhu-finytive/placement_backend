package com.uniq.placement.dto.user;

import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserCreateDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Role is required")
    private UserRole role;

    @NotNull(message = "Teams are required")
    @Size(min = 1, message = "At least one team must be assigned")
    private List<UUID> teams;

    @NotNull(message = "Status is required")
    private ActiveStatus status;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
