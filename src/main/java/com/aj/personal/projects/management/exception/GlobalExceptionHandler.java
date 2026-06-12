package com.aj.personal.projects.management.exception;

import com.aj.personal.projects.management.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorResponseDto> handleResourceNotFound(AppException ex) {
    ErrorResponseDto response = ErrorResponseDto.builder()
            .success(false)
            .error(ex.getError())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();

    return new ResponseEntity<>(response, ex.getStatus());
  }
}