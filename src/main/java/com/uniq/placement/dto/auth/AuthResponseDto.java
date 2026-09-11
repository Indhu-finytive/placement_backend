package com.uniq.placement.dto.auth;

import com.uniq.placement.dto.user.UserResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserResponseDto user;
}
