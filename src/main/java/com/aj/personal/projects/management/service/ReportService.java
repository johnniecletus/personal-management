package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.MonthlyOverviewDto;
import com.aj.personal.projects.management.dto.SavingsHistoryDto;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<MonthlyOverviewDto> getMonthlyOverviews(LocalDate from, LocalDate to, Long currencyId);

    List<SavingsHistoryDto> getSavingsHistories(Long incomeId, Long clusterId);
}
