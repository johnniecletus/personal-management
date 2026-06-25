package com.aj.personal.projects.management.dto;

import java.math.BigDecimal;
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
public class SavingsHistoryDto {
    private Long id;
    private Long incomeId;
    private String incomeName;
    private Long clusterId;
    private String clusterName;
    private Long clusterItemId;
    private Long currencyId;
    private String currencyCode;
    private String savingsName;
    private Integer percentage;
    private BigDecimal calculatedAmount;
    private LocalDateTime createdAt;
}
