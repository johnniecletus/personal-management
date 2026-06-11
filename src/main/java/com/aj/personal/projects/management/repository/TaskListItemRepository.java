package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.TaskListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListItemRepository extends JpaRepository<TaskListItem, Long> {
}
