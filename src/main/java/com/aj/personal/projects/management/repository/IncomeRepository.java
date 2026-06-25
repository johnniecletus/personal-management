package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.Income;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUserIdOrderByReceivedAtDescCreatedAtDesc(Long userId);

    Optional<Income> findByIdAndUserId(Long id, Long userId);

    boolean existsByClusterIdAndUserId(Long clusterId, Long userId);

    boolean existsByCurrencyId(Long currencyId);
}
