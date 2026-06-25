package com.aj.personal.projects.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncomePreviewRequestDto {
    @NotNull
    private Long clusterId;

    private Long currencyId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
