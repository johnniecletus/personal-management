package com.aj.personal.projects.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponseDto {
    private boolean success;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}