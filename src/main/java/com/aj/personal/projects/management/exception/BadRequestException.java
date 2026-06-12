package com.aj.personal.projects.management.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(
                message,
                "BAD_REQUEST",
                HttpStatus.BAD_REQUEST
        );
    }
}