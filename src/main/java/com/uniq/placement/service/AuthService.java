package com.uniq.placement.service;

import com.uniq.placement.dto.auth.AuthResponseDto;
import com.uniq.placement.dto.auth.LoginRequestDto;
import com.uniq.placement.dto.user.UserResponseDto;
import com.uniq.placement.entity.User;
import org.springframework.security.authentication.BadCredentialsException;
import com.uniq.placement.repository.UserRepository;
import com.uniq.placement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RolePermissionService rolePermissionService;

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateJwtToken(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            user.setLastLoginAt(Instant.now());
            userRepository.save(user);

            // Load permissions from role_permissions table (not from the JSONB user column)
            Map<String, Map<String, Boolean>> permissions =
                    rolePermissionService.getPermissionMapForRole(user.getRole());

            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(user.getId().toString());
            userDto.setName(user.getFullName());
            userDto.setUsername(user.getUsername());
            userDto.setRole(user.getRole());
            userDto.setTeams(user.getTeams().stream().map(t -> t.getId().toString()).collect(Collectors.toList()));
            userDto.setPermissions(permissions);
            userDto.setInitials(user.getInitials());

            return AuthResponseDto.builder()
                    .accessToken(jwt)
                    .expiresIn(86400)
                    .user(userDto)
                    .build();
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
