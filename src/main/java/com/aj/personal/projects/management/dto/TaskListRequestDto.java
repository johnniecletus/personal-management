package com.aj.personal.projects.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskListRequestDto {
    @NotBlank
    private String name;

    private Boolean completed;

    @Valid
    private List<TaskListItemRequestDto> items;
}
