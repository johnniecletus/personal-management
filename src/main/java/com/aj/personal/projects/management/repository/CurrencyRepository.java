package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
