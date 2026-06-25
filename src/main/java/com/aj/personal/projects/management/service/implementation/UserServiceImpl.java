package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.UpdatePasswordRequestDto;
import com.aj.personal.projects.management.dto.UpdateUserProfileRequestDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.BadRequestException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.repository.AuthSessionRepository;
import com.aj.personal.projects.management.repository.IncomeRepository;
import com.aj.personal.projects.management.repository.MonthlyOverviewRepository;
import com.aj.personal.projects.management.repository.SavingsClusterRepository;
import com.aj.personal.projects.management.repository.SavingsHistoryRepository;
import com.aj.personal.projects.management.repository.TaskListRepository;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.service.AuthService;
import com.aj.personal.projects.management.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final TaskListRepository taskListRepository;
    private final SavingsClusterRepository savingsClusterRepository;
    private final IncomeRepository incomeRepository;
    private final SavingsHistoryRepository savingsHistoryRepository;
    private final MonthlyOverviewRepository monthlyOverviewRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(this::mapUserToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        return mapUserToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        return mapUserToDto(authService.getCurrentUser());
    }

    @Override
    public UserDto updateCurrentUser(UpdateUserProfileRequestDto request) {
        User user = authService.getCurrentUser();

        if (!StringUtils.hasText(request.getEmail())
                && !StringUtils.hasText(request.getFullName())
                && !StringUtils.hasText(request.getUserName())) {
            throw new BadRequestException("At least one profile field must be provided");
        }

        if (StringUtils.hasText(request.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .ifPresent(existingUser -> {
                        throw new BadRequestException("Email " + request.getEmail() + " already exists");
                    });
            user.setEmail(request.getEmail().trim());
        }

        if (StringUtils.hasText(request.getUserName())) {
            userRepository.findByUserName(request.getUserName())
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .ifPresent(existingUser -> {
                        throw new BadRequestException("Username " + request.getUserName() + " already exists");
                    });
            user.setUserName(request.getUserName().trim());
        }

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }

        return mapUserToDto(userRepository.save(user));
    }

    @Override
    public String updateCurrentUserPassword(UpdatePasswordRequestDto request) {
        User user = authService.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password updated successfully";
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        deleteUserDependencies(user);
    }

    @Override
    public void deleteCurrentUser() {
        deleteUserDependencies(authService.getCurrentUser());
    }

    private void deleteUserDependencies(User user) {
        authSessionRepository.deleteAllByUserId(user.getId());
        savingsHistoryRepository.deleteAllByUserId(user.getId());
        monthlyOverviewRepository.deleteAllByUserId(user.getId());
        incomeRepository.deleteAll(incomeRepository.findAllByUserIdOrderByReceivedAtDescCreatedAtDesc(user.getId()));
        taskListRepository.deleteAll(taskListRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()));
        savingsClusterRepository.deleteAll(savingsClusterRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()));
        userRepository.delete(user);
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userName(user.getUserName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
