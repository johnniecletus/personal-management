package com.aj.personal.projects.management.service.implementation;


import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.exception.AppException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;



    @Override
    public Page<UserDto> getAllUsers(int page, int limit) {

        Pageable pageable = PageRequest.of(page - 1, limit);


        Page<User> usersPage = userRepository.findAll(pageable);

       return  usersPage.map(user -> UserDto.builder()
                       .id(user.getId())
                       .email(user.getEmail())
                       .fullName(user.getFullName())
                       .userName(user.getUserName())
                       .createdAt(user.getCreatedAt())
                       .updatedAt(user.getUpdatedAt())
                       .build()
               );
    }

    @Override
    public UserDto getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("User not found with id " + id));

        return UserDto.builder().id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserDto updateUserDetails(UserDto request) {
        return null;
    }

    @Override
    public String updateUserPassword(CreateUserRequestDto request) {
        return "";
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public void deleteMe(Long id) {

    }
}
