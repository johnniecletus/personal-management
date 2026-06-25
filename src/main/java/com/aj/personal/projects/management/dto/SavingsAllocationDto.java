package com.aj.personal.projects.management.dto;

import java.math.BigDecimal;
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
public class SavingsAllocationDto {
    private Long clusterItemId;
    private String savingsName;
    private Integer percentage;
    private BigDecimal calculatedAmount;
    private boolean remainder;
}
