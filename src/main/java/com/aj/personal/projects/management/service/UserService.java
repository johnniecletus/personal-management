package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(CreateUserRequestDto request);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);



}
