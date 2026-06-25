package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.Currency;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    List<Currency> findAllByOrderByCodeAsc();

    Optional<Currency> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
