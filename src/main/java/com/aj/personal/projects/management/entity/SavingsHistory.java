package com.aj.personal.projects.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "savingshistory")
public class SavingsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "income_id", nullable = false)
    private Income income;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "cluster_id", nullable = false)
    private SavingsCluster cluster;

    @ManyToOne
    @JoinColumn(name = "cluster_item_id", nullable = false)
    private SavingsClusterItem clusterItem;

    @Column(nullable = false)
    private String savings_name;

    @Column(nullable = false)
    private Integer percentage;

    @Column(nullable=false, precision = 19, scale = 2)
    private BigDecimal calculated_amount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public SavingsHistory(
            User user,
            Income income,
            Currency currency,
            SavingsCluster cluster,
            SavingsClusterItem clusterItem,
            String savings_name,
            Integer percentage,
            BigDecimal calculated_amount
    ){
        this.user = user;
        this.income = income;
        this.currency = currency;
        this.cluster = cluster;
        this.clusterItem = clusterItem;
        this.savings_name = savings_name;
        this.percentage = percentage;
        this.calculated_amount = calculated_amount;
    }
}
