package com.aj.personal.projects.management.service.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto addUser(CreateUserRequestDto request) {

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
            request.getEmail(), 
            request.getFullName(), 
            request.getUserName(), 
            hashedPassword
        );

        User savedUser = userRepository.save(user);

        return UserDto.builder().id(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .userName(savedUser.getUserName())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();

    }
}
