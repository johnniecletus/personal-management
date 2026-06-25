package com.aj.personal.projects.management.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private UserDto user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long accessTokenExpiresInSeconds;
    private Long refreshTokenExpiresInSeconds;
}
