package com.uniq.placement.dto.user;

import com.uniq.placement.entity.enums.AccessLevel;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class UserUpdateDto {
    private String name;
    private String mobile;
    @Email(message = "Invalid email format")
    private String email;
    private String username;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    private UserRole role;
    private List<UUID> teams;
    private AccessLevel access;
    private ActiveStatus status;
    private Map<String, Map<String, Boolean>> permissions;
}
