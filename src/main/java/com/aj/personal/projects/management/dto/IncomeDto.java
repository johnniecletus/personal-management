package com.aj.personal.projects.management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class IncomeDto {
    private Long id;
    private String name;
    private Long clusterId;
    private String clusterName;
    private Long currencyId;
    private String currencyCode;
    private BigDecimal amount;
    private LocalDateTime receivedAt;
    private String description;
    private BigDecimal remainderAmount;
    private List<SavingsAllocationDto> allocations;
    private LocalDateTime createdAt;
}
