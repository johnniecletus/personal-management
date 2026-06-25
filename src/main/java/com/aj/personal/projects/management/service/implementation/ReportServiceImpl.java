package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.MonthlyOverviewDto;
import com.aj.personal.projects.management.dto.SavingsHistoryDto;
import com.aj.personal.projects.management.entity.MonthlyOverview;
import com.aj.personal.projects.management.entity.SavingsHistory;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.repository.MonthlyOverviewRepository;
import com.aj.personal.projects.management.repository.SavingsHistoryRepository;
import com.aj.personal.projects.management.service.AuthService;
import com.aj.personal.projects.management.service.ReportService;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final MonthlyOverviewRepository monthlyOverviewRepository;
    private final SavingsHistoryRepository savingsHistoryRepository;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyOverviewDto> getMonthlyOverviews(LocalDate from, LocalDate to, Long currencyId) {
        User currentUser = authService.getCurrentUser();
        List<MonthlyOverview> overviews = currencyId == null
                ? monthlyOverviewRepository.findAllByUserIdOrderByMonthStartDesc(currentUser.getId())
                : monthlyOverviewRepository.findAllByUserIdAndCurrencyIdOrderByMonthStartDesc(currentUser.getId(), currencyId);

        return overviews.stream()
                .filter(overview -> from == null || !overview.getMonthStart().isBefore(from))
                .filter(overview -> to == null || !overview.getMonthStart().isAfter(to))
                .map(this::mapMonthlyOverview)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsHistoryDto> getSavingsHistories(Long incomeId, Long clusterId) {
        User currentUser = authService.getCurrentUser();
        List<SavingsHistory> histories;

        if (incomeId != null) {
            histories = savingsHistoryRepository.findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(incomeId, currentUser.getId());
        } else if (clusterId != null) {
            histories = savingsHistoryRepository.findAllByUserIdAndClusterIdOrderByCreatedAtDesc(currentUser.getId(), clusterId);
        } else {
            histories = savingsHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(currentUser.getId());
        }

        return histories.stream()
                .map(this::mapSavingsHistory)
                .toList();
    }

    private MonthlyOverviewDto mapMonthlyOverview(MonthlyOverview overview) {
        return MonthlyOverviewDto.builder()
                .id(overview.getId())
                .currencyId(overview.getCurrency().getId())
                .currencyCode(overview.getCurrency().getCode())
                .monthStart(overview.getMonthStart())
                .totalIncomeAmount(overview.getTotalIncomeAmount())
                .totalSavingsAmount(overview.getTotalSavingsAmount())
                .createdAt(overview.getCreatedAt())
                .updatedAt(overview.getUpdatedAt())
                .build();
    }

    private SavingsHistoryDto mapSavingsHistory(SavingsHistory history) {
        return SavingsHistoryDto.builder()
                .id(history.getId())
                .incomeId(history.getIncome().getId())
                .incomeName(history.getIncome().getName())
                .clusterId(history.getCluster().getId())
                .clusterName(history.getCluster().getName())
                .clusterItemId(history.getClusterItem() == null ? null : history.getClusterItem().getId())
                .currencyId(history.getCurrency().getId())
                .currencyCode(history.getCurrency().getCode())
                .savingsName(history.getSavingsName())
                .percentage(history.getPercentage())
                .calculatedAmount(history.getCalculatedAmount())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
