package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.SavingsHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsHistoryRepository extends JpaRepository<SavingsHistory, Long> {
    List<SavingsHistory> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<SavingsHistory> findAllByIncomeIdAndUserIdOrderByCreatedAtAsc(Long incomeId, Long userId);

    List<SavingsHistory> findAllByUserIdAndClusterIdOrderByCreatedAtDesc(Long userId, Long clusterId);

    void deleteAllByUserId(Long userId);

    boolean existsByCurrencyId(Long currencyId);
}
