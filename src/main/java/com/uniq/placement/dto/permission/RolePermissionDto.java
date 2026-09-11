package com.uniq.placement.dto.permission;

import com.uniq.placement.entity.enums.UserRole;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RolePermissionDto {
    private UUID id;
    private UserRole role;
    private String moduleName;
    private Boolean canView;
    private Boolean canCreate;
    private Boolean canEdit;
    private Boolean canDelete;
    private Instant updatedAt;
}
