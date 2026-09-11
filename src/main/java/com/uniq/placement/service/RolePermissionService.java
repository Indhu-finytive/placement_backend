package com.uniq.placement.service;

import com.uniq.placement.dto.permission.RolePermissionDto;
import com.uniq.placement.dto.permission.RolePermissionUpdateDto;
import com.uniq.placement.entity.RolePermission;
import com.uniq.placement.entity.enums.UserRole;
import com.uniq.placement.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public static final List<String> ALL_MODULES = List.of(
        "Dashboard", "Candidates", "Collections",
        "Share & Settlement", "Payment Accounts",
        "Reports", "User Management", "Settings"
    );

    @Transactional(readOnly = true)
    public List<RolePermissionDto> getPermissionsForRole(UserRole role) {
        return rolePermissionRepository.findByRole(role)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Returns a flat map: { moduleName: {canView, canCreate, canEdit, canDelete} }
     * Used for login response / frontend permission check.
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, Boolean>> getPermissionMapForRole(UserRole role) {
        // Admin always has full access — no DB lookup needed
        if (role == UserRole.ADMIN) {
            return ALL_MODULES.stream().collect(Collectors.toMap(
                m -> m,
                m -> Map.of("View", true, "Create", true, "Edit", true, "Delete", true)
            ));
        }
        return rolePermissionRepository.findByRole(role).stream()
                .collect(Collectors.toMap(
                    RolePermission::getModuleName,
                    rp -> Map.of(
                        "View",   Boolean.TRUE.equals(rp.getCanView()),
                        "Create", Boolean.TRUE.equals(rp.getCanCreate()),
                        "Edit",   Boolean.TRUE.equals(rp.getCanEdit()),
                        "Delete", Boolean.TRUE.equals(rp.getCanDelete())
                    )
                ));
    }

    @Transactional
    public List<RolePermissionDto> savePermissionsForRole(UserRole role, List<RolePermissionUpdateDto> updates) {
        // Delete existing and replace
        rolePermissionRepository.deleteByRole(role);
        rolePermissionRepository.flush();

        List<RolePermission> saved = updates.stream().map(dto -> {
            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setModuleName(dto.getModuleName());
            rp.setCanView(Boolean.TRUE.equals(dto.getCanView()));
            rp.setCanCreate(Boolean.TRUE.equals(dto.getCanCreate()));
            rp.setCanEdit(Boolean.TRUE.equals(dto.getCanEdit()));
            rp.setCanDelete(Boolean.TRUE.equals(dto.getCanDelete()));
            return rolePermissionRepository.save(rp);
        }).collect(Collectors.toList());

        return saved.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RolePermissionDto> getAllRolePermissions() {
        return rolePermissionRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private RolePermissionDto toDto(RolePermission rp) {
        RolePermissionDto dto = new RolePermissionDto();
        dto.setId(rp.getId());
        dto.setRole(rp.getRole());
        dto.setModuleName(rp.getModuleName());
        dto.setCanView(rp.getCanView());
        dto.setCanCreate(rp.getCanCreate());
        dto.setCanEdit(rp.getCanEdit());
        dto.setCanDelete(rp.getCanDelete());
        dto.setUpdatedAt(rp.getUpdatedAt());
        return dto;
    }
}
