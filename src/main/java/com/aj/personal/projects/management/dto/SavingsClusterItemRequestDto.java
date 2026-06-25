package com.aj.personal.projects.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingsClusterItemRequestDto {
    @NotBlank
    private String name;

    @Min(0)
    @Max(100)
    private Integer percentage;
}
