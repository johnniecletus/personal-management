package com.aj.personal.projects.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private T data;
    private String message;
}
