package com.uniq.placement.dto.user;

import com.uniq.placement.entity.enums.AccessLevel;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.UserRole;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class UserResponseDto {
    private String id;
    private String name;
    private String mobile;
    private String email;
    private String username;
    private UserRole role;
    private List<String> teams;
    private AccessLevel access;
    private ActiveStatus status;
    private Map<String, Map<String, Boolean>> permissions;
    private String initials;
    private Instant lastLoginAt;
}
