package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.TaskListDto;
import com.aj.personal.projects.management.dto.TaskListRequestDto;
import java.util.List;

public interface TaskListService {

    TaskListDto createTask(TaskListRequestDto task);

    TaskListDto updateTask(TaskListRequestDto task, Long id);

    TaskListDto getTask(Long id);

    void deleteTask(Long id);

    List<TaskListDto> getAllTasks();

    TaskListDto competeAllTaskItems(Long id);
}
