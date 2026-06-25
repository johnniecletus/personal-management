package com.aj.personal.projects.management.dto;

import java.math.BigDecimal;
import java.util.List;
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
public class IncomePreviewDto {
    private Long clusterId;
    private BigDecimal amount;
    private Integer totalPercentage;
    private Integer remainderPercentage;
    private BigDecimal totalAllocatedAmount;
    private BigDecimal remainderAmount;
    private List<SavingsAllocationDto> allocations;
}
