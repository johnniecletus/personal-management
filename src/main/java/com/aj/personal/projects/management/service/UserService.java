package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;

public interface UserService {

    UserDto addUser(CreateUserRequestDto request);

}
