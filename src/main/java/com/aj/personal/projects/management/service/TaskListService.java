package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.TaskListDto;

import java.util.List;

public interface TaskListService {

    TaskListDto createTask(TaskListDto task);

    TaskListDto updateTask(TaskListDto task, Long id);

    TaskListDto getTask(Long id);

    void deleteTask(Long id);

    List<TaskListDto> getAllTasks();

    TaskListDto competeAllTaskItems(Long id);


}
