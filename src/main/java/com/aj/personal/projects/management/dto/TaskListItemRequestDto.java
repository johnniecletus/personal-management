package com.aj.personal.projects.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskListItemRequestDto {
    @NotBlank
    private String name;

    @DecimalMin(value = "0.00")
    private BigDecimal amount;

    private Boolean completed;
}
