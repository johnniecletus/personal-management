package com.aj.personal.projects.management.security;

import com.aj.personal.projects.management.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (
                token != null &&
                        jwtTokenProvider.validateAccessToken(token) &&
                        SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            String usernameOrEmail = jwtTokenProvider.getUsernameFromToken(token);

            List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRolesFromToken(token)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    usernameOrEmail,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (
                bearerToken != null &&
                        bearerToken.startsWith("Bearer ")
        ) {
            return bearerToken.substring(7);
        }

        if (
                request.getCookies() == null
        ) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
