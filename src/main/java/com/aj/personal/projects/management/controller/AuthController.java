package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.*;
import com.aj.personal.projects.management.service.AuthService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserDto>> createUser(
            CreateUserRequestDto request
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
    public ResponseEntity<ApiResponseDto<UserDto>> loginUser(
            @RequestBody LoginUserRequestDto request,
            HttpServletResponse response
    ) {

        LoginResponseDto loginResponse = authService.loginUser(request);

        ResponseCookie accessCookie = ResponseCookie.from(
                        "access_token",
                        loginResponse.getAccessToken()
                )
                .httpOnly(true)
                .secure(false) // true in production HTTPS
                .path("/")
                .sameSite("Lax")
                .maxAge(15 * 60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refresh_token",
                        loginResponse.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false) // true in production HTTPS
                .path("/api/v1/auth/refresh")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        ApiResponseDto<UserDto> apiresponse = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User created successfully")
                .data(loginResponse.getUser())
                .build();

        return new ResponseEntity<>(apiresponse, HttpStatus.OK);
    }
}
