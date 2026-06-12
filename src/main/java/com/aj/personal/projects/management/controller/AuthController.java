package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserDto>> createUsers(CreateUserRequestDto request) {
        UserDto user = authService.createUser(request);

        ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User created successfully")
                .data(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
