package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.LoginUserRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.AppException;
import com.aj.personal.projects.management.exception.BadRequestException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with " + email));
    }

    @Override
    public UserDto createUser(CreateUserRequestDto request) {

        if (userRepository.existsByUsername(request.getUserName())) {
            throw new BadRequestException("Username " + request.getUserName() + " already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email " + request.getEmail() + " already exists");
        }

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

    @Override
    public UserDto loginUser(LoginUserRequestDto request) {

       if(userRepository.findByEmailOrUsername(request.getEmailOrUsername(), request.getEmailOrUsername())){
           throw new ResourceNotFoundException("User does not exit with" + request.getEmailOrUsername());
       }


        return null;
    }
}
