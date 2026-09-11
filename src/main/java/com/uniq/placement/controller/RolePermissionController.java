package com.uniq.placement.controller;

import com.uniq.placement.dto.permission.RolePermissionDto;
import com.uniq.placement.dto.permission.RolePermissionUpdateDto;
import com.uniq.placement.entity.enums.UserRole;
import com.uniq.placement.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    /**
     * Get all role permissions (Admin only — for the permissions management page).
     */
    @GetMapping
    public ResponseEntity<List<RolePermissionDto>> getAllRolePermissions() {
        return ResponseEntity.ok(rolePermissionService.getAllRolePermissions());
    }

    /**
     * Get permissions for a specific role.
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<RolePermissionDto>> getPermissionsForRole(@PathVariable UserRole role) {
        return ResponseEntity.ok(rolePermissionService.getPermissionsForRole(role));
    }

    /**
     * Get permissions for the currently logged-in user's role.
     * Called by the frontend immediately after login.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Map<String, Boolean>>> getMyPermissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // Get the role from the JWT principal's authorities
        UserRole role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> {
                    String roleName = a.getAuthority().replace("ROLE_", "");
                    return UserRole.fromValue(roleName.charAt(0) + roleName.substring(1).toLowerCase().replace("_", " "));
                })
                .orElse(UserRole.CUSTOM_USER);
        return ResponseEntity.ok(rolePermissionService.getPermissionMapForRole(role));
    }

    /**
     * Save/replace all permissions for a role (Admin only).
     */
    @PutMapping("/role/{role}")
    public ResponseEntity<List<RolePermissionDto>> savePermissionsForRole(
            @PathVariable UserRole role,
            @Valid @RequestBody List<RolePermissionUpdateDto> permissions) {
        return ResponseEntity.ok(rolePermissionService.savePermissionsForRole(role, permissions));
    }
}
