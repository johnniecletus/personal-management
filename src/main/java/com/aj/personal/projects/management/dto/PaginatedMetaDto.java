package com.aj.personal.projects.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaginatedMetaDto {
    private  int page;
    private int limit;
    private Long total;
    private int totalPages;
}
