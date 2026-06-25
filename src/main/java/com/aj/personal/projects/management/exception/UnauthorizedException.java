package com.aj.personal.projects.management.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(
                message,
                "UNAUTHORIZED",
                HttpStatus.UNAUTHORIZED
        );
    }
}
