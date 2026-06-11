package com.aj.personal.projects.management.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "savingcluseritems")
public class SavingsClusterItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "cluster_id", nullable = false)
    private SavingsCluster savingsCluster;

    @Column(nullable = false)
    private Integer Percentage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public SavingsClusterItem(String name, SavingsCluster savingsCluster, Integer Percentage) {
        this.name = name;
        this.savingsCluster = savingsCluster;
        this.Percentage = Percentage;
    }
}
