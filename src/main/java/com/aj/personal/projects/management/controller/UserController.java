package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.PaginatedMetaDto;
import com.aj.personal.projects.management.dto.PaginatedResponseDto;
import com.aj.personal.projects.management.dto.UpdatePasswordRequestDto;
import com.aj.personal.projects.management.dto.UpdateUserProfileRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<List<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UserDto>> getUser(@PathVariable("id") Long id) {
        UserDto user = userService.getUserById(id);

        ApiResponseDto<UserDto> result = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User fetched successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<UserDto>> getCurrentUser() {
        ApiResponseDto<UserDto> result = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("Current user fetched successfully")
                .data(userService.getCurrentUserProfile())
                .build();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponseDto<UserDto>> updateCurrentUser(
            @Valid @RequestBody UpdateUserProfileRequestDto request
    ) {
        ApiResponseDto<UserDto> result = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(userService.updateCurrentUser(request))
                .build();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponseDto<String>> updateCurrentUserPassword(
            @Valid @RequestBody UpdatePasswordRequestDto request
    ) {
        ApiResponseDto<String> result = ApiResponseDto.<String>builder()
                .success(true)
                .message("Password updated successfully")
                .data(userService.updateCurrentUserPassword(request))
                .build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponseDto<String>> deleteCurrentUser() {
        userService.deleteCurrentUser();

        ApiResponseDto<String> result = ApiResponseDto.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .data("User deleted")
                .build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<String>> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);

        ApiResponseDto<String> result = ApiResponseDto.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .data("User deleted")
                .build();

        return ResponseEntity.ok(result);
    }
}
