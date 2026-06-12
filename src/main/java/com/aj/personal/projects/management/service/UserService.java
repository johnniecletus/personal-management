package com.aj.personal.projects.management.service;


import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import org.springframework.data.domain.Page;


public interface UserService {

    Page<UserDto> getAllUsers(int page, int limit);

    UserDto getUserById(Long id);

    UserDto updateUserDetails(UserDto request);

    String updateUserPassword(CreateUserRequestDto request);

    void deleteUser(Long id);

    void deleteMe(Long id);





}
