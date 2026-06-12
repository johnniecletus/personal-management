package com.aj.personal.projects.management.exception;


import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super(
                message,
                "NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}
