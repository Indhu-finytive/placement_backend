package com.uniq.placement.dto.user;

import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserUpdateDto {
    private String name;
    private String mobile;
    @Email(message = "Invalid email format")
    private String email;
    private String username;
    
    private String password;
    
    private UserRole role;
    private List<String> teams;
    private ActiveStatus status;
}
