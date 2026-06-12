package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.TaskListDto;
import com.aj.personal.projects.management.entity.TaskList;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.repository.TaskListRepository;
import com.aj.personal.projects.management.service.AuthService;
import com.aj.personal.projects.management.service.TaskListService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskListServiceImpl implements TaskListService {

   private TaskListRepository taskListRepository;
   private final AuthService authService;

    @Override
    public TaskListDto createTask(TaskListDto task) {

        User currentUser = authService.getCurrentUser();

        TaskList newTask = new TaskList(
                currentUser,
                task.getName()
        );

        TaskList savedTask = taskListRepository.save(newTask);

        return TaskListDto.builder()
                .id(savedTask.getId())
                .name(savedTask.getName())
                .completed(savedTask.isCompleted())
                .createdAt(savedTask.getCreatedAt())
                .updatedAt(savedTask.getUpdatedAt())
                .build();
    }

    @Override
    public TaskListDto updateTask(TaskListDto task, Long id) {
        return null;
    }

    @Override
    public TaskListDto getTask(Long id) {
        return null;
    }

    @Override
    public void deleteTask(Long id) {

    }

    @Override
    public List<TaskListDto> getAllTasks() {
        return List.of();
    }

    @Override
    public TaskListDto competeAllTaskItems(Long id) {
        return null;
    }
}
