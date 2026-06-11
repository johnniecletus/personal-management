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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "savingshistories")
public class SavingsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_id", nullable = false)
    private Income income;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private SavingsCluster cluster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_item_id")
    private SavingsClusterItem clusterItem;

    @Column(name = "savings_name", nullable = false)
    private String savingsName;

    @Column(nullable = false)
    private Integer percentage;

    @Column(name = "calculated_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal calculatedAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SavingsHistory(
            User user,
            Income income,
            Currency currency,
            SavingsCluster cluster,
            SavingsClusterItem clusterItem,
            String savingsName,
            Integer percentage,
            BigDecimal calculatedAmount
    ) {
        this.user = user;
        this.income = income;
        this.currency = currency;
        this.cluster = cluster;
        this.clusterItem = clusterItem;
        this.savingsName = savingsName;
        this.percentage = percentage;
        this.calculatedAmount = calculatedAmount == null ? BigDecimal.ZERO : calculatedAmount;
    }
}
