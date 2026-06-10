package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import org.springframework.data.domain.Page;


public interface UserService {

    UserDto addUser(CreateUserRequestDto request);

    Page<UserDto> getAllUsers(int page, int limit);

    UserDto getUserById(Long id);



}
