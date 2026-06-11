package com.aj.personal.projects.management.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskListItemDto {
    private Long id;
    private String name;
    private BigDecimal amount;
    private boolean completed;
}
