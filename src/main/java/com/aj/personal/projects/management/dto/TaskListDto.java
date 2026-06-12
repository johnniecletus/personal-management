package com.aj.personal.projects.management.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskListDto {

    private Long id;

    private String name;

    private boolean completed;

    private List<TaskListItemDto> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
