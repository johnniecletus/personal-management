package com.aj.personal.projects.management.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;

import com.aj.personal.projects.management.dto.IncomeDto;
import com.aj.personal.projects.management.dto.IncomeRequestDto;
import com.aj.personal.projects.management.entity.Currency;
import com.aj.personal.projects.management.entity.MonthlyOverview;
import com.aj.personal.projects.management.entity.SavingsCluster;
import com.aj.personal.projects.management.entity.SavingsClusterItem;
import com.aj.personal.projects.management.entity.SavingsHistory;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.repository.CurrencyRepository;
import com.aj.personal.projects.management.repository.MonthlyOverviewRepository;
import com.aj.personal.projects.management.repository.SavingsClusterRepository;
import com.aj.personal.projects.management.repository.SavingsHistoryRepository;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.service.IncomeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class IncomeServiceImplTest {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private SavingsClusterRepository savingsClusterRepository;

    @Autowired
    private SavingsHistoryRepository savingsHistoryRepository;

    @Autowired
    private MonthlyOverviewRepository monthlyOverviewRepository;

    private User user;
    private Currency currency;
    private SavingsCluster cluster;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(
                "income-test@example.com",
                "Income Test User",
                "income-test-user",
                "encoded-password"
        ));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );

        currency = currencyRepository.findByCodeIgnoreCase("NGN").orElseThrow();
        cluster = new SavingsCluster(user, "salary");
        cluster.getItems().add(new SavingsClusterItem("emergency savings", cluster, 30));
        cluster.getItems().add(new SavingsClusterItem("main savings", cluster, 20));
        cluster = savingsClusterRepository.save(cluster);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createIncomeCreatesSavingsHistoryAndMonthlyOverview() {
        LocalDateTime receivedAt = LocalDateTime.of(2026, 6, 12, 10, 15);
        IncomeRequestDto request = new IncomeRequestDto(
                "Salary June",
                cluster.getId(),
                currency.getId(),
                new BigDecimal("123.45"),
                receivedAt,
                "June salary"
        );

        IncomeDto income = incomeService.createIncome(request);
        List<SavingsHistory> histories = savingsHistoryRepository.findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(
                income.getId(),
                user.getId()
        );

        MonthlyOverview overview = monthlyOverviewRepository
                .findByUserIdAndCurrencyIdAndMonthStart(user.getId(), currency.getId(), LocalDate.of(2026, 6, 1))
                .orElseThrow();

        assertThat(income.getRemainderAmount()).isEqualByComparingTo("61.73");
        assertThat(histories).hasSize(3);
        assertThat(histories.get(0).getCalculatedAmount()).isEqualByComparingTo("37.03");
        assertThat(histories.get(1).getCalculatedAmount()).isEqualByComparingTo("24.69");
        assertThat(histories.get(2).getCalculatedAmount()).isEqualByComparingTo("61.73");
        assertThat(overview.getTotalIncomeAmount()).isEqualByComparingTo("123.45");
        assertThat(overview.getTotalSavingsAmount()).isEqualByComparingTo("123.45");
    }
}
