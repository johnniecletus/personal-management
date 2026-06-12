package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/api/v1/auth/csrf")
    public ResponseEntity<ApiResponseDto<CsrfToken>> getCsrf(HttpServletRequest request) {
        CsrfToken csrfTOken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        ApiResponseDto<CsrfToken> response = ApiResponseDto.<CsrfToken>builder()
                .success(true)
                .data(csrfTOken)
                .message("Csrf token fetched successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}