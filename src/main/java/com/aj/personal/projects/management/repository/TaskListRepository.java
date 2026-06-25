package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.TaskList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<TaskList> findByIdAndUserId(Long id, Long userId);
}
