package com.aj.personal.projects.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "monthlyoverviews")
public class MonthlyOverview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime month;

    @Column(nullable=false, precision = 19, scale = 2)
    private BigDecimal total_income_amount = BigDecimal.ZERO;

    @Column(nullable=false, precision = 19, scale = 2)
    private BigDecimal total_savings_amount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public MonthlyOverview(User user, Currency currency, BigDecimal total_income_amount, BigDecimal total_savings_amount) {
        this.user = user;
        this.currency = currency;
        this.total_income_amount = total_income_amount;
        this.total_savings_amount = total_savings_amount;
    }

}
