package com.uniq.placement.repository;

import com.uniq.placement.entity.RolePermission;
import com.uniq.placement.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRole(UserRole role);

    Optional<RolePermission> findByRoleAndModuleName(UserRole role, String moduleName);

    void deleteByRole(UserRole role);
}
