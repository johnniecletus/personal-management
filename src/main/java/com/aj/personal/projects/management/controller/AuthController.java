package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.LoginResponseDto;
import com.aj.personal.projects.management.dto.LoginUserRequestDto;
import com.aj.personal.projects.management.dto.UserSessionDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.security.AuthCookieService;
import com.aj.personal.projects.management.security.JwtTokenProvider;
import com.aj.personal.projects.management.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthCookieService authCookieService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserDto>> createUser(
            @Valid @RequestBody CreateUserRequestDto request
    ) {
        UserDto user = authService.createUser(request);

        ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User created successfully")
                .data(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> loginUser(
            @Valid @RequestBody LoginUserRequestDto request,
            HttpServletResponse response
    ) {
        LoginResponseDto loginResponse = authService.loginUser(request);
        authCookieService.addSessionCookies(response, loginResponse);

        ApiResponseDto<LoginResponseDto> apiResponse = ApiResponseDto.<LoginResponseDto>builder()
                .success(true)
                .message("Login successful")
                .data(loginResponse)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logoutSession(authCookieService.getRefreshTokenFromCookie(request));
        authCookieService.clearSessionCookies(response);

        ApiResponseDto<String> apiResponse = ApiResponseDto.<String>builder()
                .success(true)
                .message("Logout successful")
                .data("Logged out")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<UserDto>> getCurrentUser() {
        UserDto user = authService.getCurrentUserProfile();

        ApiResponseDto<UserDto> apiResponse = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("Current user fetched successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponseDto<List<UserSessionDto>>> getCurrentUserSessions() {
        return ResponseEntity.ok(
                ApiResponseDto.<List<UserSessionDto>>builder()
                        .success(true)
                        .message("Sessions fetched successfully")
                        .data(authService.getCurrentUserSessions())
                        .build()
        );
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponseDto<String>> revokeCurrentUserSession(
            @PathVariable String sessionId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.revokeCurrentUserSession(sessionId);

        if (isCurrentSession(sessionId, request)) {
            authCookieService.clearSessionCookies(response);
        }

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("Session revoked successfully")
                        .data("Session revoked")
                        .build()
        );
    }

    @GetMapping("/users/{userId}/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<UserSessionDto>>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponseDto.<List<UserSessionDto>>builder()
                        .success(true)
                        .message("User sessions fetched successfully")
                        .data(authService.getUserSessions(userId))
                        .build()
        );
    }

    @DeleteMapping("/users/{userId}/sessions/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<String>> revokeUserSession(
            @PathVariable Long userId,
            @PathVariable String sessionId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.revokeUserSession(userId, sessionId);

        if (isCurrentSession(sessionId, request)) {
            authCookieService.clearSessionCookies(response);
        }

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("User session revoked successfully")
                        .data("User session revoked")
                        .build()
        );
    }

    @DeleteMapping("/users/{userId}/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<String>> revokeAllUserSessions(@PathVariable Long userId) {
        authService.revokeAllUserSessions(userId);

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("User sessions revoked successfully")
                        .data("User sessions revoked")
                        .build()
        );
    }

    private boolean isCurrentSession(String sessionId, HttpServletRequest request) {
        String accessToken = authCookieService.getAccessTokenFromCookie(request);
        if (accessToken != null && jwtTokenProvider.validateAccessToken(accessToken)) {
            return sessionId.equals(jwtTokenProvider.getSessionIdFromToken(accessToken));
        }

        String refreshToken = authCookieService.getRefreshTokenFromCookie(request);
        if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return sessionId.equals(jwtTokenProvider.getSessionIdFromToken(refreshToken));
        }

        return false;
    }
}
