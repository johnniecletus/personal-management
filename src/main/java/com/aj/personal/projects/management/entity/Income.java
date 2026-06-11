package com.aj.personal.projects.management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "incomes")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private SavingsCluster cluster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column
    private String description;

    @OneToMany(mappedBy = "income", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsHistory> savingsHistories = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Income(
            User user,
            String name,
            SavingsCluster cluster,
            Currency currency,
            BigDecimal amount,
            LocalDateTime receivedAt,
            String description
    ) {
        this.user = user;
        this.name = name;
        this.cluster = cluster;
        this.currency = currency;
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.receivedAt = receivedAt;
        this.description = description;
    }
}
