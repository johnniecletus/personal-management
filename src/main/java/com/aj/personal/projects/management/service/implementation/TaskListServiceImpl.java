package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.TaskListDto;
import com.aj.personal.projects.management.dto.TaskListItemDto;
import com.aj.personal.projects.management.dto.TaskListItemRequestDto;
import com.aj.personal.projects.management.dto.TaskListRequestDto;
import com.aj.personal.projects.management.entity.TaskList;
import com.aj.personal.projects.management.entity.TaskListItem;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.repository.TaskListRepository;
import com.aj.personal.projects.management.service.AuthService;
import com.aj.personal.projects.management.service.TaskListService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepository taskListRepository;
    private final AuthService authService;

    @Override
    public TaskListDto createTask(TaskListRequestDto task) {
        User currentUser = authService.getCurrentUser();

        TaskList newTask = new TaskList(currentUser, task.getName().trim());
        replaceTaskItems(newTask, task.getItems());
        newTask.setCompleted(resolveTaskCompletion(task.getCompleted(), newTask.getItems()));

        return mapTask(taskListRepository.save(newTask));
    }

    @Override
    public TaskListDto updateTask(TaskListRequestDto task, Long id) {
        TaskList existingTask = getOwnedTask(id);
        existingTask.setName(task.getName().trim());

        if (task.getItems() != null) {
            replaceTaskItems(existingTask, task.getItems());
        }

        existingTask.setCompleted(resolveUpdatedCompletion(existingTask, task));

        return mapTask(taskListRepository.save(existingTask));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskListDto getTask(Long id) {
        return mapTask(getOwnedTask(id));
    }

    @Override
    public void deleteTask(Long id) {
        taskListRepository.delete(getOwnedTask(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskListDto> getAllTasks() {
        User currentUser = authService.getCurrentUser();
        return taskListRepository.findAllByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapTask)
                .toList();
    }

    @Override
    public TaskListDto competeAllTaskItems(Long id) {
        TaskList taskList = getOwnedTask(id);
        taskList.getItems().forEach(item -> item.setCompleted(true));
        taskList.setCompleted(true);
        return mapTask(taskListRepository.save(taskList));
    }

    private TaskList getOwnedTask(Long id) {
        User currentUser = authService.getCurrentUser();
        return taskListRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task list not found with id " + id));
    }

    private void replaceTaskItems(TaskList taskList, List<TaskListItemRequestDto> requestItems) {
        taskList.getItems().clear();

        if (requestItems == null) {
            return;
        }

        for (TaskListItemRequestDto requestItem : requestItems) {
            TaskListItem item = new TaskListItem(
                    taskList,
                    requestItem.getName().trim(),
                    requestItem.getAmount(),
                    requestItem.getCompleted()
            );
            taskList.getItems().add(item);
        }
    }

    private boolean resolveTaskCompletion(Boolean requestedCompletion, List<TaskListItem> items) {
        if (!items.isEmpty()) {
            boolean allItemsComplete = items.stream().allMatch(TaskListItem::isCompleted);
            return allItemsComplete || Boolean.TRUE.equals(requestedCompletion);
        }

        return Boolean.TRUE.equals(requestedCompletion);
    }

    private boolean resolveUpdatedCompletion(TaskList existingTask, TaskListRequestDto request) {
        if (request.getCompleted() == null && request.getItems() == null) {
            return existingTask.isCompleted();
        }

        return resolveTaskCompletion(request.getCompleted(), existingTask.getItems());
    }

    private TaskListDto mapTask(TaskList taskList) {
        return TaskListDto.builder()
                .id(taskList.getId())
                .name(taskList.getName())
                .completed(taskList.isCompleted())
                .items(taskList.getItems().stream().map(this::mapTaskItem).toList())
                .createdAt(taskList.getCreatedAt())
                .updatedAt(taskList.getUpdatedAt())
                .build();
    }

    private TaskListItemDto mapTaskItem(TaskListItem item) {
        return TaskListItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .amount(item.getAmount())
                .completed(item.isCompleted())
                .build();
    }
}
