package com.aj.personal.projects.management.entity;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        name = "savingsclusteritems",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_savingsclusteritems_cluster_name",
                columnNames = {"cluster_id", "name"}
        )
)
public class SavingsClusterItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private SavingsCluster savingsCluster;

    @Column(nullable = false)
    private Integer percentage = 0;

    @OneToMany(mappedBy = "clusterItem")
    private List<SavingsHistory> savingsHistories = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SavingsClusterItem(String name, SavingsCluster savingsCluster, Integer percentage) {
        this.name = name;
        this.savingsCluster = savingsCluster;
        this.percentage = percentage == null ? 0 : percentage;
    }
}
