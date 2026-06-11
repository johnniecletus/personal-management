package com.aj.personal.projects.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SavingsHistory")
public class SavingsHisory {
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

    private String savings_name;

    private Integer percentage;

    private BigDecimal calculated_amount;
}
