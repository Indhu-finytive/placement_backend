package com.uniq.placement.dto.permission;

import com.uniq.placement.entity.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePermissionUpdateDto {
    @NotNull
    private UserRole role;
    @NotBlank
    private String moduleName;
    private Boolean canView = false;
    private Boolean canCreate = false;
    private Boolean canEdit = false;
    private Boolean canDelete = false;
}
