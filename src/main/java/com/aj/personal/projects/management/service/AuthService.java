package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.LoginResponseDto;
import com.aj.personal.projects.management.dto.LoginUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;

public interface AuthService {

    User getCurrentUser();

    UserDto createUser(CreateUserRequestDto request);

    LoginResponseDto loginUser(LoginUserRequestDto request);
}
