package com.aj.personal.projects.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "monthlyoverviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_monthlyoverviews_user_currency_month_start",
                columnNames = {"user_id", "currency_id", "month_start"}
        )
)
public class MonthlyOverview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(name = "month_start", nullable = false)
    private LocalDate monthStart;

    @Column(name = "total_income_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalIncomeAmount = BigDecimal.ZERO;

    @Column(name = "total_savings_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSavingsAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MonthlyOverview(
            User user,
            Currency currency,
            LocalDate monthStart,
            BigDecimal totalIncomeAmount,
            BigDecimal totalSavingsAmount
    ) {
        this.user = user;
        this.currency = currency;
        this.monthStart = monthStart;
        this.totalIncomeAmount = totalIncomeAmount == null ? BigDecimal.ZERO : totalIncomeAmount;
        this.totalSavingsAmount = totalSavingsAmount == null ? BigDecimal.ZERO : totalSavingsAmount;
    }
}
