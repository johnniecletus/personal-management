package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.MonthlyOverview;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyOverviewRepository extends JpaRepository<MonthlyOverview, Long> {
    Optional<MonthlyOverview> findByUserIdAndCurrencyIdAndMonthStart(Long userId, Long currencyId, LocalDate monthStart);

    List<MonthlyOverview> findAllByUserIdOrderByMonthStartDesc(Long userId);

    List<MonthlyOverview> findAllByUserIdAndCurrencyIdOrderByMonthStartDesc(Long userId, Long currencyId);

    void deleteAllByUserId(Long userId);

    boolean existsByCurrencyId(Long currencyId);
}
