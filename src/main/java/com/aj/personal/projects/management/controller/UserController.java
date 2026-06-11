package com.aj.personal.projects.management.controller;


import com.aj.personal.projects.management.dto.*;
import com.aj.personal.projects.management.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/new")
    public ResponseEntity<ApiResponseDto<UserDto>> createUsers(CreateUserRequestDto request) {
        UserDto user = userService.addUser(request);

        ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User created successfully")
                .data(user)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<List<UserDto>>> getAllUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {

        Page<UserDto> usersPage = userService.getAllUsers(page, limit);

        PaginatedMetaDto pagination = PaginatedMetaDto.builder()
                .page(page)
                .limit(limit)
                .total(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .build();

        PaginatedResponseDto<List<UserDto>> response = PaginatedResponseDto.<List<UserDto>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(usersPage.getContent())
                .pagination(pagination)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUser(@PathVariable("id") Long id ) {

        UserDto user = userService.getUserById(id);

        ApiResponseDto<UserDto> result = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User fetched successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(result);
    }

}
