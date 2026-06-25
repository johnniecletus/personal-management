package com.aj.personal.projects.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyOverviewDto {
    private Long id;
    private Long currencyId;
    private String currencyCode;
    private LocalDate monthStart;
    private BigDecimal totalIncomeAmount;
    private BigDecimal totalSavingsAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
