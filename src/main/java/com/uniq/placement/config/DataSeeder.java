package com.uniq.placement.config;

import com.uniq.placement.entity.RolePermission;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.AccessLevel;
import com.uniq.placement.entity.enums.UserRole;
import com.uniq.placement.repository.RolePermissionRepository;
import com.uniq.placement.repository.UserRepository;
import com.uniq.placement.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedAdminUser();
        seedRolePermissions();
    }

    private void seedAdminUser() {
        if (userRepository.count() > 0) return;

        User admin = new User();
        admin.setFullName("Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@uniq.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.ADMIN);
        admin.setIsActive(true);
        admin.setAccess(AccessLevel.FULL_ACCESS);

        userRepository.save(admin);
        System.out.println("✅ Seeded default Admin user (admin / admin123)");
    }

    private void seedRolePermissions() {
        if (rolePermissionRepository.count() > 0) return;

        List<String> modules = RolePermissionService.ALL_MODULES;

        // --- ADMIN: full access to everything ---
        for (String module : modules) {
            save(UserRole.ADMIN, module, true, true, true, true);
        }

        // --- COLLECTION USER: can view/add/edit candidates & collections, view reports/dashboard ---
        save(UserRole.COLLECTION_USER, "Dashboard",           true,  false, false, false);
        save(UserRole.COLLECTION_USER, "Candidates",          true,  true,  true,  false);
        save(UserRole.COLLECTION_USER, "Collections",         true,  true,  true,  false);
        save(UserRole.COLLECTION_USER, "Share & Settlement",  false, false, false, false);
        save(UserRole.COLLECTION_USER, "Payment Accounts",    false, false, false, false);
        save(UserRole.COLLECTION_USER, "Reports",             true,  false, false, false);
        save(UserRole.COLLECTION_USER, "User Management",     false, false, false, false);
        save(UserRole.COLLECTION_USER, "Settings",            false, false, false, false);

        // --- SHARE PARTNER: can view candidates, collections, share & settlement, reports ---
        save(UserRole.SHARE_PARTNER, "Dashboard",           true,  false, false, false);
        save(UserRole.SHARE_PARTNER, "Candidates",          true,  false, false, false);
        save(UserRole.SHARE_PARTNER, "Collections",         true,  false, false, false);
        save(UserRole.SHARE_PARTNER, "Share & Settlement",  true,  false, false, false);
        save(UserRole.SHARE_PARTNER, "Payment Accounts",    false, false, false, false);
        save(UserRole.SHARE_PARTNER, "Reports",             true,  false, false, false);
        save(UserRole.SHARE_PARTNER, "User Management",     false, false, false, false);
        save(UserRole.SHARE_PARTNER, "Settings",            false, false, false, false);

        // --- CUSTOM USER: view only dashboard and candidates ---
        save(UserRole.CUSTOM_USER, "Dashboard",           true,  false, false, false);
        save(UserRole.CUSTOM_USER, "Candidates",          true,  false, false, false);
        save(UserRole.CUSTOM_USER, "Collections",         false, false, false, false);
        save(UserRole.CUSTOM_USER, "Share & Settlement",  false, false, false, false);
        save(UserRole.CUSTOM_USER, "Payment Accounts",    false, false, false, false);
        save(UserRole.CUSTOM_USER, "Reports",             false, false, false, false);
        save(UserRole.CUSTOM_USER, "User Management",     false, false, false, false);
        save(UserRole.CUSTOM_USER, "Settings",            false, false, false, false);

        System.out.println("✅ Seeded role_permissions table for all 4 roles");
    }

    private void save(UserRole role, String module,
                      boolean view, boolean create, boolean edit, boolean delete) {
        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setModuleName(module);
        rp.setCanView(view);
        rp.setCanCreate(create);
        rp.setCanEdit(edit);
        rp.setCanDelete(delete);
        rolePermissionRepository.save(rp);
    }
}
