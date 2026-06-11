package com.aj.personal.projects.management.repository;

import com.aj.personal.projects.management.entity.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListRepository  extends JpaRepository<TaskList, Long> {
}
