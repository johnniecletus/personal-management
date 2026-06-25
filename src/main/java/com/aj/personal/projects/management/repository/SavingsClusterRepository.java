package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.SavingsCluster;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsClusterRepository extends JpaRepository<SavingsCluster, Long> {
    List<SavingsCluster> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SavingsCluster> findByIdAndUserId(Long id, Long userId);

    Optional<SavingsCluster> findByUserIdAndNameIgnoreCase(Long userId, String name);
}
