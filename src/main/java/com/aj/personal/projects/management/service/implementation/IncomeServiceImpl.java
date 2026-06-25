package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.IncomeDto;
import com.aj.personal.projects.management.dto.IncomePreviewDto;
import com.aj.personal.projects.management.dto.IncomePreviewRequestDto;
import com.aj.personal.projects.management.dto.IncomeRequestDto;
import com.aj.personal.projects.management.dto.SavingsAllocationDto;
import com.aj.personal.projects.management.entity.Currency;
import com.aj.personal.projects.management.entity.Income;
import com.aj.personal.projects.management.entity.MonthlyOverview;
import com.aj.personal.projects.management.entity.SavingsCluster;
import com.aj.personal.projects.management.entity.SavingsClusterItem;
import com.aj.personal.projects.management.entity.SavingsHistory;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.BadRequestException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.repository.CurrencyRepository;
import com.aj.personal.projects.management.repository.IncomeRepository;
import com.aj.personal.projects.management.repository.MonthlyOverviewRepository;
import com.aj.personal.projects.management.repository.SavingsClusterRepository;
import com.aj.personal.projects.management.repository.SavingsHistoryRepository;
import com.aj.personal.projects.management.service.AuthService;
import com.aj.personal.projects.management.service.IncomeService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class IncomeServiceImpl implements IncomeService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private final IncomeRepository incomeRepository;
    private final SavingsClusterRepository savingsClusterRepository;
    private final CurrencyRepository currencyRepository;
    private final SavingsHistoryRepository savingsHistoryRepository;
    private final MonthlyOverviewRepository monthlyOverviewRepository;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public IncomePreviewDto previewIncome(IncomePreviewRequestDto request) {
        User currentUser = authService.getCurrentUser();
        SavingsCluster cluster = getOwnedCluster(request.getClusterId(), currentUser.getId());

        if (request.getCurrencyId() != null) {
            getCurrencyEntity(request.getCurrencyId());
        }

        AllocationResult allocationResult = calculateAllocations(cluster, request.getAmount());
        return mapPreview(cluster, request.getAmount(), allocationResult);
    }

    @Override
    public IncomeDto createIncome(IncomeRequestDto request) {
        User currentUser = authService.getCurrentUser();
        SavingsCluster cluster = getOwnedCluster(request.getClusterId(), currentUser.getId());
        Currency currency = getCurrencyEntity(request.getCurrencyId());
        LocalDateTime receivedAt = request.getReceivedAt() == null ? LocalDateTime.now() : request.getReceivedAt();

        AllocationResult allocationResult = calculateAllocations(cluster, request.getAmount());

        Income income = new Income(
                currentUser,
                request.getName().trim(),
                cluster,
                currency,
                normalizeMoney(request.getAmount()),
                receivedAt,
                request.getDescription()
        );

        Income savedIncome = incomeRepository.save(income);
        List<SavingsHistory> histories = buildSavingsHistories(currentUser, savedIncome, currency, cluster, allocationResult);
        savingsHistoryRepository.saveAll(histories);

        updateMonthlyOverview(
                currentUser,
                currency,
                receivedAt.toLocalDate(),
                savedIncome.getAmount(),
                allocationResult.totalAllocatedAmount()
        );

        return mapIncome(savedIncome, allocationResult);
    }

    @Override
    public IncomeDto updateIncome(Long id, IncomeRequestDto request) {
        User currentUser = authService.getCurrentUser();
        Income income = getOwnedIncome(id, currentUser.getId());
        List<SavingsHistory> existingHistories = savingsHistoryRepository
                .findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(income.getId(), currentUser.getId());
        BigDecimal existingSavingsAmount = sumSavings(existingHistories);

        updateMonthlyOverview(
                currentUser,
                income.getCurrency(),
                income.getReceivedAt().toLocalDate(),
                income.getAmount().negate(),
                existingSavingsAmount.negate()
        );

        savingsHistoryRepository.deleteAll(existingHistories);

        SavingsCluster newCluster = getOwnedCluster(request.getClusterId(), currentUser.getId());
        Currency newCurrency = getCurrencyEntity(request.getCurrencyId());
        LocalDateTime receivedAt = request.getReceivedAt() == null ? income.getReceivedAt() : request.getReceivedAt();
        AllocationResult allocationResult = calculateAllocations(newCluster, request.getAmount());

        income.setName(request.getName().trim());
        income.setCluster(newCluster);
        income.setCurrency(newCurrency);
        income.setAmount(normalizeMoney(request.getAmount()));
        income.setReceivedAt(receivedAt);
        income.setDescription(request.getDescription());

        Income savedIncome = incomeRepository.save(income);
        List<SavingsHistory> histories = buildSavingsHistories(currentUser, savedIncome, newCurrency, newCluster, allocationResult);
        savingsHistoryRepository.saveAll(histories);

        updateMonthlyOverview(
                currentUser,
                newCurrency,
                receivedAt.toLocalDate(),
                savedIncome.getAmount(),
                allocationResult.totalAllocatedAmount()
        );

        return mapIncome(savedIncome, allocationResult);
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeDto getIncome(Long id) {
        User currentUser = authService.getCurrentUser();
        Income income = getOwnedIncome(id, currentUser.getId());
        AllocationResult allocationResult = allocationResultFromHistory(
                savingsHistoryRepository.findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(id, currentUser.getId())
        );

        return mapIncome(income, allocationResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeDto> getAllIncomes() {
        User currentUser = authService.getCurrentUser();
        return incomeRepository.findAllByUserIdOrderByReceivedAtDescCreatedAtDesc(currentUser.getId())
                .stream()
                .map(income -> mapIncome(
                        income,
                        allocationResultFromHistory(
                                savingsHistoryRepository.findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(
                                        income.getId(),
                                        currentUser.getId()
                                )
                        )
                ))
                .toList();
    }

    @Override
    public void deleteIncome(Long id) {
        User currentUser = authService.getCurrentUser();
        Income income = getOwnedIncome(id, currentUser.getId());
        List<SavingsHistory> histories = savingsHistoryRepository
                .findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(income.getId(), currentUser.getId());

        updateMonthlyOverview(
                currentUser,
                income.getCurrency(),
                income.getReceivedAt().toLocalDate(),
                income.getAmount().negate(),
                sumSavings(histories).negate()
        );

        savingsHistoryRepository.deleteAll(histories);
        incomeRepository.delete(income);
    }

    private SavingsCluster getOwnedCluster(Long clusterId, Long userId) {
        return savingsClusterRepository.findByIdAndUserId(clusterId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings cluster not found with id " + clusterId));
    }

    private Income getOwnedIncome(Long id, Long userId) {
        return incomeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id " + id));
    }

    private Currency getCurrencyEntity(Long id) {
        return currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id " + id));
    }

    private AllocationResult calculateAllocations(SavingsCluster cluster, BigDecimal amount) {
        BigDecimal normalizedAmount = normalizeMoney(amount);
        List<SavingsClusterItem> items = cluster.getItems();
        int totalPercentage = items.stream().map(SavingsClusterItem::getPercentage).reduce(0, Integer::sum);

        if (totalPercentage > 100) {
            throw new BadRequestException("The linked savings cluster exceeds 100 percent");
        }

        int remainderPercentage = 100 - totalPercentage;
        List<ResolvedAllocation> allocations = new ArrayList<>();
        BigDecimal allocatedAmount = ZERO;

        for (int index = 0; index < items.size(); index++) {
            SavingsClusterItem item = items.get(index);
            boolean isLastUserItem = index == items.size() - 1;

            BigDecimal amountForItem;
            if (remainderPercentage == 0 && isLastUserItem) {
                amountForItem = normalizedAmount.subtract(allocatedAmount).setScale(2, RoundingMode.HALF_UP);
            } else {
                amountForItem = percentageAmount(normalizedAmount, item.getPercentage());
            }

            allocations.add(new ResolvedAllocation(
                    item,
                    item.getName(),
                    item.getPercentage(),
                    amountForItem,
                    false
            ));
            allocatedAmount = allocatedAmount.add(amountForItem);
        }

        BigDecimal remainderAmount = normalizedAmount.subtract(allocatedAmount).setScale(2, RoundingMode.HALF_UP);

        if (remainderAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Income allocation produced an invalid negative remainder");
        }

        if (remainderPercentage > 0) {
            allocations.add(new ResolvedAllocation(
                    null,
                    "Remainder",
                    remainderPercentage,
                    remainderAmount,
                    true
            ));
            allocatedAmount = allocatedAmount.add(remainderAmount);
        } else {
            remainderAmount = ZERO;
        }

        return new AllocationResult(
                cluster.getId(),
                normalizedAmount,
                totalPercentage,
                remainderPercentage,
                allocatedAmount,
                remainderAmount,
                allocations
        );
    }

    private List<SavingsHistory> buildSavingsHistories(
            User user,
            Income income,
            Currency currency,
            SavingsCluster cluster,
            AllocationResult allocationResult
    ) {
        List<SavingsHistory> histories = new ArrayList<>();

        for (ResolvedAllocation allocation : allocationResult.allocations()) {
            if (allocation.amount().compareTo(BigDecimal.ZERO) == 0 && allocation.remainder()) {
                continue;
            }

            histories.add(new SavingsHistory(
                    user,
                    income,
                    currency,
                    cluster,
                    allocation.clusterItem(),
                    allocation.savingsName(),
                    allocation.percentage(),
                    allocation.amount()
            ));
        }

        return histories;
    }

    private void updateMonthlyOverview(
            User user,
            Currency currency,
            LocalDate date,
            BigDecimal incomeDelta,
            BigDecimal savingsDelta
    ) {
        LocalDate monthStart = date.withDayOfMonth(1);
        MonthlyOverview overview = monthlyOverviewRepository
                .findByUserIdAndCurrencyIdAndMonthStart(user.getId(), currency.getId(), monthStart)
                .orElseGet(() -> new MonthlyOverview(user, currency, monthStart, ZERO, ZERO));

        overview.setTotalIncomeAmount(normalizeMoney(overview.getTotalIncomeAmount().add(incomeDelta)));
        overview.setTotalSavingsAmount(normalizeMoney(overview.getTotalSavingsAmount().add(savingsDelta)));

        if (overview.getTotalIncomeAmount().compareTo(BigDecimal.ZERO) <= 0
                && overview.getTotalSavingsAmount().compareTo(BigDecimal.ZERO) <= 0
                && overview.getId() != null) {
            monthlyOverviewRepository.delete(overview);
            return;
        }

        monthlyOverviewRepository.save(overview);
    }

    private IncomePreviewDto mapPreview(SavingsCluster cluster, BigDecimal amount, AllocationResult allocationResult) {
        return IncomePreviewDto.builder()
                .clusterId(cluster.getId())
                .amount(normalizeMoney(amount))
                .totalPercentage(allocationResult.totalPercentage())
                .remainderPercentage(allocationResult.remainderPercentage())
                .totalAllocatedAmount(allocationResult.totalAllocatedAmount())
                .remainderAmount(allocationResult.remainderAmount())
                .allocations(mapAllocations(allocationResult.allocations()))
                .build();
    }

    private IncomeDto mapIncome(Income income, AllocationResult allocationResult) {
        return IncomeDto.builder()
                .id(income.getId())
                .name(income.getName())
                .clusterId(income.getCluster().getId())
                .clusterName(income.getCluster().getName())
                .currencyId(income.getCurrency().getId())
                .currencyCode(income.getCurrency().getCode())
                .amount(income.getAmount())
                .receivedAt(income.getReceivedAt())
                .description(income.getDescription())
                .remainderAmount(allocationResult.remainderAmount())
                .allocations(mapAllocations(allocationResult.allocations()))
                .createdAt(income.getCreatedAt())
                .build();
    }

    private List<SavingsAllocationDto> mapAllocations(List<ResolvedAllocation> allocations) {
        return allocations.stream()
                .map(allocation -> SavingsAllocationDto.builder()
                        .clusterItemId(allocation.clusterItem() == null ? null : allocation.clusterItem().getId())
                        .savingsName(allocation.savingsName())
                        .percentage(allocation.percentage())
                        .calculatedAmount(allocation.amount())
                        .remainder(allocation.remainder())
                        .build())
                .toList();
    }

    private AllocationResult allocationResultFromHistory(List<SavingsHistory> histories) {
        int remainderPercentage = histories.stream()
                .filter(history -> history.getClusterItem() == null)
                .map(SavingsHistory::getPercentage)
                .findFirst()
                .orElse(0);

        BigDecimal remainderAmount = histories.stream()
                .filter(history -> history.getClusterItem() == null)
                .map(SavingsHistory::getCalculatedAmount)
                .findFirst()
                .orElse(ZERO);

        int totalPercentage = histories.stream()
                .filter(history -> history.getClusterItem() != null)
                .map(SavingsHistory::getPercentage)
                .reduce(0, Integer::sum);

        BigDecimal totalAllocatedAmount = histories.stream()
                .map(SavingsHistory::getCalculatedAmount)
                .reduce(ZERO, BigDecimal::add);

        BigDecimal incomeAmount = histories.isEmpty()
                ? ZERO
                : histories.get(0).getIncome().getAmount();

        List<ResolvedAllocation> allocations = histories.stream()
                .map(history -> new ResolvedAllocation(
                        history.getClusterItem(),
                        history.getSavingsName(),
                        history.getPercentage(),
                        history.getCalculatedAmount(),
                        history.getClusterItem() == null
                ))
                .toList();

        return new AllocationResult(
                histories.isEmpty() ? null : histories.get(0).getCluster().getId(),
                incomeAmount,
                totalPercentage,
                remainderPercentage,
                totalAllocatedAmount,
                remainderAmount,
                allocations
        );
    }

    private BigDecimal percentageAmount(BigDecimal amount, Integer percentage) {
        return amount.multiply(BigDecimal.valueOf(percentage))
                .divide(ONE_HUNDRED, 2, RoundingMode.DOWN);
    }

    private BigDecimal sumSavings(List<SavingsHistory> histories) {
        return histories.stream()
                .map(SavingsHistory::getCalculatedAmount)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private record ResolvedAllocation(
            SavingsClusterItem clusterItem,
            String savingsName,
            Integer percentage,
            BigDecimal amount,
            boolean remainder
    ) {
    }

    private record AllocationResult(
            Long clusterId,
            BigDecimal amount,
            Integer totalPercentage,
            Integer remainderPercentage,
            BigDecimal totalAllocatedAmount,
            BigDecimal remainderAmount,
            List<ResolvedAllocation> allocations
    ) {
    }
}
