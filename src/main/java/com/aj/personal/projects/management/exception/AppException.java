package com.aj.personal.projects.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;
    private final String error;

    public AppException(
            String message,
            String error,
            HttpStatus status
    ) {
        super(message);
        this.error = error;
        this.status = status;
    }
}
