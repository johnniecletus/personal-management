package com.aj.personal.projects.management.dto;

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
public class SavingsClusterDto {
    private Long id;
    private String name;
    private Integer totalPercentage;
    private Integer remainderPercentage;
    private List<SavingsClusterItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
