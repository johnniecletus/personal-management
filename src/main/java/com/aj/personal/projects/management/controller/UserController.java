package com.aj.personal.projects.management.controller;


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
    public ResponseEntity<UserDto> createUsers(CreateUserRequestDto request) {
        UserDto user = userService.addUser(request);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id ) {

        UserDto user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

}
