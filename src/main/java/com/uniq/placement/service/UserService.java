package com.uniq.placement.service;

import com.uniq.placement.dto.common.PageDto;
import com.uniq.placement.dto.user.UserCreateDto;
import com.uniq.placement.dto.user.UserResponseDto;
import com.uniq.placement.dto.user.UserStatusUpdateDto;
import com.uniq.placement.dto.user.UserUpdateDto;
import com.uniq.placement.entity.Team;
import com.uniq.placement.entity.User;
import com.uniq.placement.entity.enums.AccessLevel;
import com.uniq.placement.entity.enums.ActiveStatus;
import com.uniq.placement.entity.enums.UserRole;
import com.uniq.placement.exception.BusinessRuleException;
import com.uniq.placement.exception.DuplicateResourceException;
import com.uniq.placement.exception.ResourceNotFoundException;
import com.uniq.placement.repository.TeamRepository;
import com.uniq.placement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionService rolePermissionService;

    @Transactional(readOnly = true)
    public PageDto<UserResponseDto> getUsers(String search, UserRole role, UUID teamId, ActiveStatus status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Boolean isActive = status == null ? null : status == ActiveStatus.ACTIVE;
        
        Page<User> userPage = userRepository.findAllWithFilters(search, role, teamId, isActive, pageable);
        
        List<UserResponseDto> dtos = userPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        return new PageDto<>(dtos, page, pageSize, userPage.getTotalElements());
    }

    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        User user = new User();
        user.setFullName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setMobileNumber(dto.getMobile());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setAccess(accessForRole(dto.getRole()));
        user.setIsActive(dto.getStatus() == ActiveStatus.ACTIVE);
        user.setPermissions(rolePermissionService.getPermissionMapForRole(dto.getRole()));

        List<Team> teams = teamRepository.findAllById(dto.getTeams());
        if (teams.size() != dto.getTeams().size()) {
            throw new ResourceNotFoundException("One or more teams were not found");
        }
        user.setTeams(new HashSet<>(teams));

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return mapToDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) user.setFullName(dto.getName().trim());
        if (dto.getMobile() != null) user.setMobileNumber(dto.getMobile().trim());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail().trim());
        
        if (dto.getUsername() != null && !dto.getUsername().isBlank() && !dto.getUsername().trim().equals(user.getUsername())) {
            String trimmedUsername = dto.getUsername().trim();
            if (userRepository.existsByUsername(trimmedUsername)) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(trimmedUsername);
        }
        
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            String trimmedPass = dto.getPassword().trim();
            if (trimmedPass.length() < 8) {
                throw new BusinessRuleException("Password must be at least 8 characters");
            }
            user.setPasswordHash(passwordEncoder.encode(trimmedPass));
        }
        
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
            user.setAccess(accessForRole(dto.getRole()));
            user.setPermissions(rolePermissionService.getPermissionMapForRole(dto.getRole()));
        }
        if (dto.getStatus() != null) user.setIsActive(dto.getStatus() == ActiveStatus.ACTIVE);

        if (dto.getTeams() != null) {
            Set<Team> resolvedTeams = new HashSet<>();
            for (String teamRef : dto.getTeams()) {
                if (teamRef == null || teamRef.isBlank() || teamRef.equalsIgnoreCase("All teams")) {
                    continue;
                }
                try {
                    UUID teamUuid = UUID.fromString(teamRef.trim());
                    teamRepository.findById(teamUuid).ifPresent(resolvedTeams::add);
                } catch (IllegalArgumentException e) {
                    teamRepository.findByNameIgnoreCase(teamRef.trim()).ifPresent(resolvedTeams::add);
                }
            }
            user.setTeams(resolvedTeams);
        }

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.getRole() == UserRole.ADMIN && user.getUsername().equals("admin")) {
            throw new BusinessRuleException("Primary admin cannot be deleted");
        }
        
        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDto updateStatus(UUID id, UserStatusUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setIsActive(dto.getStatus() == ActiveStatus.ACTIVE);
        return mapToDto(userRepository.save(user));
    }

    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId().toString());
        dto.setName(user.getFullName());
        dto.setMobile(user.getMobileNumber());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setTeams(user.getTeams().stream().map(t -> t.getId().toString()).collect(Collectors.toList()));
        dto.setStatus(user.getIsActive() ? ActiveStatus.ACTIVE : ActiveStatus.INACTIVE);
        // Load permissions from role_permissions table
        dto.setPermissions(rolePermissionService.getPermissionMapForRole(user.getRole()));
        dto.setInitials(user.getInitials());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }

    private AccessLevel accessForRole(UserRole role) {
        if (role == UserRole.ADMIN) return AccessLevel.FULL_ACCESS;
        if (role == UserRole.SHARE_PARTNER) return AccessLevel.SHARE_VIEW_ONLY;
        if (role == UserRole.CUSTOM_USER) return AccessLevel.VIEW_ONLY;
        return AccessLevel.COLLECTION_ENTRY;
    }
}
