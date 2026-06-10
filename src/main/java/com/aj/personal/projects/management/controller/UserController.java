package com.aj.personal.projects.management.controller;


import com.aj.personal.projects.management.dto.ApiResponse;
import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUsers(CreateUserRequestDto request) {
        UserDto user = userService.addUser(request);

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .success(true)
                .message("User created successfully")
                .data(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        ApiResponse<List<UserDto>> response = ApiResponse.<List<UserDto>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(users)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable("id") Long id ) {

        UserDto user = userService.getUserById(id);

        ApiResponse<UserDto> result = ApiResponse.<UserDto>builder()
                .success(true)
                .message("User fetched successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(result);
    }

}
