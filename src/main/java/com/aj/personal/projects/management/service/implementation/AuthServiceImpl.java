package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.LoginResponseDto;
import com.aj.personal.projects.management.dto.LoginUserRequestDto;
import com.aj.personal.projects.management.dto.UserSessionDto;
import com.aj.personal.projects.management.entity.AuthSession;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.BadRequestException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.exception.UnauthorizedException;
import com.aj.personal.projects.management.repository.AuthSessionRepository;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.security.JwtTokenProvider;
import com.aj.personal.projects.management.service.AuthService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Authentication is required");
        }

        String emailOrUserName = authentication.getName();

        return userRepository.findByEmailOrUserName(emailOrUserName, emailOrUserName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with " + emailOrUserName));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        return mapUserToDto(getCurrentUser());
    }

    @Override
    public UserDto createUser(CreateUserRequestDto request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new BadRequestException("Username " + request.getUserName() + " already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email " + request.getEmail() + " already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
                request.getEmail().trim(),
                request.getFullName().trim(),
                request.getUserName().trim(),
                hashedPassword
        );

        return mapUserToDto(userRepository.save(user));
    }

    @Override
    public LoginResponseDto loginUser(LoginUserRequestDto request) {
        User user = userRepository.findByEmailOrUserName(
                request.getEmailOrUsername(),
                request.getEmailOrUsername()
        ).orElseThrow(() -> new ResourceNotFoundException(
                "User does not exit with " + request.getEmailOrUsername()
        ));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new UnauthorizedException("Password incorrect");
        }

        AuthSession session = createSession(user);
        return rotateSessionTokens(session);
    }

    @Override
    public LoginResponseDto refreshSession(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token is invalid or expired");
        }

        AuthSession session = getSessionFromToken(refreshToken);
        validateRefreshSession(session, refreshToken);

        return rotateSessionTokens(session);
    }

    @Override
    @Transactional(readOnly = true)
    public User getActiveSessionUser(String sessionId) {
        AuthSession session = authSessionRepository.findByPublicId(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Session is invalid"));

        if (!isSessionActive(session)) {
            throw new UnauthorizedException("Session is no longer active");
        }

        return session.getUser();
    }

    @Override
    public void logoutSession(String refreshToken) {
        revokeSessionByRefreshToken(refreshToken, "Logged out");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSessionDto> getCurrentUserSessions() {
        return getUserSessions(getCurrentUser().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSessionDto> getUserSessions(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        return authSessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapSessionToDto)
                .toList();
    }

    @Override
    public void revokeCurrentUserSession(String sessionId) {
        revokeOwnedSession(getCurrentUser().getId(), sessionId, "Revoked by user");
    }

    @Override
    public void revokeUserSession(Long userId, String sessionId) {
        revokeOwnedSession(userId, sessionId, "Revoked by admin");
    }

    @Override
    public void revokeAllUserSessions(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        authSessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(this::isSessionActive)
                .forEach(session -> revokeSession(session, "Revoked by admin"));
    }

    private AuthSession createSession(User user) {
        AuthSession session = new AuthSession();
        session.setPublicId(UUID.randomUUID().toString());
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(
                jwtTokenProvider.getRefreshTokenExpirationMilliseconds()
        )));
        session.setLastUsedAt(LocalDateTime.now());
        session.setRefreshTokenHash("");

        return authSessionRepository.save(session);
    }

    private LoginResponseDto rotateSessionTokens(AuthSession session) {
        Authentication authentication = buildAuthentication(session.getUser());
        Instant sessionExpiry = session.getExpiresAt()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication, session.getPublicId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                authentication,
                session.getPublicId(),
                sessionExpiry
        );

        session.setRefreshTokenHash(hashToken(refreshToken));
        session.setLastUsedAt(LocalDateTime.now());
        authSessionRepository.save(session);

        return LoginResponseDto.builder()
                .user(mapUserToDto(session.getUser()))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresInSeconds(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .refreshTokenExpiresInSeconds(getRemainingSessionSeconds(session))
                .build();
    }

    private Authentication buildAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private AuthSession getSessionFromToken(String refreshToken) {
        String sessionId = jwtTokenProvider.getSessionIdFromToken(refreshToken);

        return authSessionRepository.findByPublicId(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Refresh session is invalid"));
    }

    private void validateRefreshSession(AuthSession session, String refreshToken) {
        if (!isSessionActive(session)) {
            throw new UnauthorizedException("Refresh session is no longer active");
        }

        if (!hashToken(refreshToken).equals(session.getRefreshTokenHash())) {
            throw new UnauthorizedException("Refresh token is no longer valid");
        }
    }

    private boolean isSessionActive(AuthSession session) {
        return session.getRevokedAt() == null && session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    private long getRemainingSessionSeconds(AuthSession session) {
        return Math.max(Duration.between(LocalDateTime.now(), session.getExpiresAt()).getSeconds(), 0);
    }

    private void revokeOwnedSession(Long userId, String sessionId, String reason) {
        AuthSession session = authSessionRepository.findByPublicIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id " + sessionId));

        revokeSession(session, reason);
    }

    private void revokeSessionByRefreshToken(String refreshToken, String reason) {
        if (refreshToken == null || refreshToken.isBlank() || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return;
        }

        authSessionRepository.findByPublicId(jwtTokenProvider.getSessionIdFromToken(refreshToken))
                .ifPresent(session -> {
                    if (hashToken(refreshToken).equals(session.getRefreshTokenHash())) {
                        revokeSession(session, reason);
                    }
                });
    }

    private void revokeSession(AuthSession session, String reason) {
        if (session.getRevokedAt() != null) {
            return;
        }

        session.setRevokedAt(LocalDateTime.now());
        session.setRevokedReason(reason);
        authSessionRepository.save(session);
    }

    private UserSessionDto mapSessionToDto(AuthSession session) {
        return UserSessionDto.builder()
                .sessionId(session.getPublicId())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .lastUsedAt(session.getLastUsedAt())
                .expiresAt(session.getExpiresAt())
                .revokedAt(session.getRevokedAt())
                .revokedReason(session.getRevokedReason())
                .build();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hashedBytes.length * 2);

            for (byte hashedByte : hashedBytes) {
                builder.append(String.format("%02x", hashedByte));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 hashing is not available", exception);
        }
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
