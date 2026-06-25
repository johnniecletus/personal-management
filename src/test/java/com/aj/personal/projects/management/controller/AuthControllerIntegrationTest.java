package com.aj.personal.projects.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aj.personal.projects.management.dto.CreateUserRequestDto;
import com.aj.personal.projects.management.dto.LoginUserRequestDto;
import com.aj.personal.projects.management.dto.SavingsClusterItemRequestDto;
import com.aj.personal.projects.management.dto.SavingsClusterRequestDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.enums.UserRole;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.security.AuthCookieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void protectedRequestAutomaticallyRefreshesFromRefreshCookie() throws Exception {
        SessionLogin sessionLogin = registerAndLogin(
                "auth-test@example.com",
                "auth-test-user",
                "Password123!"
        );

        mockMvc.perform(get("/api/v1/savings-clusters")
                        .cookie(invalidAccessCookie(), sessionLogin.refreshCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(cookie().exists(AuthCookieService.ACCESS_TOKEN_COOKIE_NAME))
                .andExpect(cookie().exists(AuthCookieService.REFRESH_TOKEN_COOKIE_NAME));
    }

    @Test
    void logoutRevokesSessionAndStopsSilentRefresh() throws Exception {
        SessionLogin sessionLogin = registerAndLogin(
                "logout-test@example.com",
                "logout-test-user",
                "Password123!"
        );

        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .cookie(sessionLogin.refreshCookie()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/savings-clusters")
                        .cookie(invalidAccessCookie(), sessionLogin.refreshCookie()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanRevokeUserSessionAndStopSilentRefresh() throws Exception {
        SessionLogin adminLogin = registerAndLogin(
                "admin@example.com",
                "admin-user",
                "Password123!"
        );
        promoteToAdmin(adminLogin.userId());

        SessionLogin userLogin = registerAndLogin(
                "member@example.com",
                "member-user",
                "Password123!"
        );

        MvcResult sessionsResult = mockMvc.perform(get("/api/v1/auth/users/{userId}/sessions", userLogin.userId())
                        .cookie(adminLogin.accessCookie(), adminLogin.refreshCookie()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode sessionsJson = objectMapper.readTree(sessionsResult.getResponse().getContentAsString());
        String sessionId = sessionsJson.path("data").get(0).path("sessionId").asText();
        assertThat(sessionId).isNotBlank();

        mockMvc.perform(delete("/api/v1/auth/users/{userId}/sessions/{sessionId}", userLogin.userId(), sessionId)
                        .with(csrf())
                        .cookie(adminLogin.accessCookie(), adminLogin.refreshCookie()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/savings-clusters")
                        .cookie(invalidAccessCookie(), userLogin.refreshCookie()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mutatingRequestsStillRequireCsrfWhenSilentRefreshIsUsed() throws Exception {
        SessionLogin sessionLogin = registerAndLogin(
                "csrf-test@example.com",
                "csrf-test-user",
                "Password123!"
        );

        SavingsClusterRequestDto request = new SavingsClusterRequestDto(
                "salary",
                List.of(
                        new SavingsClusterItemRequestDto("emergency savings", 30),
                        new SavingsClusterItemRequestDto("tithe", 10)
                )
        );

        mockMvc.perform(post("/api/v1/savings-clusters")
                        .cookie(invalidAccessCookie(), sessionLogin.refreshCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/savings-clusters")
                        .with(csrf())
                        .cookie(invalidAccessCookie(), sessionLogin.refreshCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.remainderPercentage").value(60))
                .andExpect(cookie().exists(AuthCookieService.ACCESS_TOKEN_COOKIE_NAME))
                .andExpect(cookie().exists(AuthCookieService.REFRESH_TOKEN_COOKIE_NAME));
    }

    private SessionLogin registerAndLogin(String email, String userName, String password) throws Exception {
        CreateUserRequestDto registerRequest = new CreateUserRequestDto(
                email,
                "Auth Test User",
                userName,
                password
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginUserRequestDto loginRequest = new LoginUserRequestDto(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value(email))
                .andReturn();

        Cookie accessCookie = loginResult.getResponse().getCookie(AuthCookieService.ACCESS_TOKEN_COOKIE_NAME);
        Cookie refreshCookie = loginResult.getResponse().getCookie(AuthCookieService.REFRESH_TOKEN_COOKIE_NAME);
        assertThat(accessCookie).isNotNull();
        assertThat(refreshCookie).isNotNull();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        long userId = loginJson.path("data").path("user").path("id").asLong();

        return new SessionLogin(userId, accessCookie, refreshCookie);
    }

    private void promoteToAdmin(Long userId) {
        User adminUser = userRepository.findById(userId)
                .orElseThrow();
        adminUser.setRole(UserRole.ADMIN);
        userRepository.save(adminUser);
    }

    private Cookie invalidAccessCookie() {
        return new Cookie(AuthCookieService.ACCESS_TOKEN_COOKIE_NAME, "expired-or-invalid");
    }

    private record SessionLogin(Long userId, Cookie accessCookie, Cookie refreshCookie) {
    }
}
