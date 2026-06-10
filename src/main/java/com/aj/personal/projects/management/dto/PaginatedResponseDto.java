package com.aj.personal.projects.management.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private PaginatedMetaDto pagination;
}
