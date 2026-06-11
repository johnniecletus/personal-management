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
        name = "savingsclusters",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_savingsclusters_user_name",
                columnNames = {"user_id", "name"}
        )
)
public class SavingsCluster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "savingsCluster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsClusterItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "cluster")
    private List<Income> incomes = new ArrayList<>();

    @OneToMany(mappedBy = "cluster")
    private List<SavingsHistory> savingsHistories = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SavingsCluster(User user, String name) {
        this.user = user;
        this.name = name;
    }
}
