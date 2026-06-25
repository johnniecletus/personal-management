package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.LoginResponseDto;
import com.aj.personal.projects.management.dto.LoginUserRequestDto;
import com.aj.personal.projects.management.dto.UserSessionDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import java.util.List;

public interface AuthService {

    User getCurrentUser();

    UserDto getCurrentUserProfile();

    UserDto createUser(CreateUserRequestDto request);

    LoginResponseDto loginUser(LoginUserRequestDto request);

    LoginResponseDto refreshSession(String refreshToken);

    User getActiveSessionUser(String sessionId);

    void logoutSession(String refreshToken);

    List<UserSessionDto> getCurrentUserSessions();

    List<UserSessionDto> getUserSessions(Long userId);

    void revokeCurrentUserSession(String sessionId);

    void revokeUserSession(Long userId, String sessionId);

    void revokeAllUserSessions(Long userId);
}
