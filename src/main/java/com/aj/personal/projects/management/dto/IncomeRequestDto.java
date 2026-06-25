package com.aj.personal.projects.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncomeRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private Long clusterId;

    @NotNull
    private Long currencyId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private LocalDateTime receivedAt;

    private String description;
}
