package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.AuthSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByPublicId(String publicId);

    Optional<AuthSession> findByPublicIdAndUserId(String publicId, Long userId);

    List<AuthSession> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteAllByUserId(Long userId);
}
