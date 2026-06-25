package com.aj.personal.projects.management.security;

import com.aj.personal.projects.management.dto.LoginResponseDto;
import com.aj.personal.projects.management.dto.UserDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.UnauthorizedException;
import com.aj.personal.projects.management.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Set<String> PUBLIC_COOKIE_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/logout",
            "/api/v1/auth/csrf"
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final AuthCookieService authCookieService;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            AuthService authService,
            AuthCookieService authCookieService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.authCookieService = authCookieService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateRequest(request, response);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            authenticateAccessToken(bearerToken.substring(7));
            return;
        }

        if (authenticateAccessToken(authCookieService.getAccessTokenFromCookie(request))) {
            return;
        }

        if (PUBLIC_COOKIE_PATHS.contains(request.getRequestURI())) {
            return;
        }

        refreshCookieBackedSession(request, response);
    }

    private boolean authenticateAccessToken(String accessToken) {
        if (accessToken == null || !jwtTokenProvider.validateAccessToken(accessToken)) {
            return false;
        }

        try {
            User user = authService.getActiveSessionUser(jwtTokenProvider.getSessionIdFromToken(accessToken));
            authenticateUser(user);
            return true;
        } catch (UnauthorizedException exception) {
            return false;
        }
    }

    private void refreshCookieBackedSession(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = authCookieService.getRefreshTokenFromCookie(request);

        if (refreshToken == null || refreshToken.isBlank()) {
            if (authCookieService.getAccessTokenFromCookie(request) != null) {
                authCookieService.clearSessionCookies(response);
            }
            return;
        }

        try {
            LoginResponseDto loginResponse = authService.refreshSession(refreshToken);
            authCookieService.addSessionCookies(response, loginResponse);
            authenticateUser(loginResponse.getUser());
        } catch (UnauthorizedException exception) {
            authCookieService.clearSessionCookies(response);
        }
    }

    private void authenticateUser(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void authenticateUser(UserDto user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
