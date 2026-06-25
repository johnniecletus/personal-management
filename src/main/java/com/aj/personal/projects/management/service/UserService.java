package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.UpdatePasswordRequestDto;
import com.aj.personal.projects.management.dto.UpdateUserProfileRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<UserDto> getAllUsers(int page, int limit);

    UserDto getUserById(Long id);

    UserDto getCurrentUserProfile();

    UserDto updateCurrentUser(UpdateUserProfileRequestDto request);

    String updateCurrentUserPassword(UpdatePasswordRequestDto request);

    void deleteUser(Long id);

    void deleteCurrentUser();
}
