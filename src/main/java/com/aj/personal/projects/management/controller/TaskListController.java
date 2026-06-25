package com.aj.personal.projects.management.controller;

import com.aj.personal.projects.management.dto.ApiResponseDto;
import com.aj.personal.projects.management.dto.TaskListDto;
import com.aj.personal.projects.management.dto.TaskListRequestDto;
import com.aj.personal.projects.management.service.TaskListService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/task-lists")
@AllArgsConstructor
public class TaskListController {
    private final TaskListService taskListService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<TaskListDto>> createTaskList(
            @Valid @RequestBody TaskListRequestDto request
    ) {
        TaskListDto taskList = taskListService.createTask(request);

        return new ResponseEntity<>(
                ApiResponseDto.<TaskListDto>builder()
                        .success(true)
                        .message("Task list created successfully")
                        .data(taskList)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<TaskListDto>>> getAllTaskLists() {
        return ResponseEntity.ok(
                ApiResponseDto.<List<TaskListDto>>builder()
                        .success(true)
                        .message("Task lists fetched successfully")
                        .data(taskListService.getAllTasks())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<TaskListDto>> getTaskList(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDto.<TaskListDto>builder()
                        .success(true)
                        .message("Task list fetched successfully")
                        .data(taskListService.getTask(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<TaskListDto>> updateTaskList(
            @PathVariable Long id,
            @Valid @RequestBody TaskListRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.<TaskListDto>builder()
                        .success(true)
                        .message("Task list updated successfully")
                        .data(taskListService.updateTask(request, id))
                        .build()
        );
    }

    @PatchMapping("/{id}/complete-all")
    public ResponseEntity<ApiResponseDto<TaskListDto>> completeAllTaskItems(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDto.<TaskListDto>builder()
                        .success(true)
                        .message("Task list items completed successfully")
                        .data(taskListService.competeAllTaskItems(id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> deleteTaskList(@PathVariable Long id) {
        taskListService.deleteTask(id);

        return ResponseEntity.ok(
                ApiResponseDto.<String>builder()
                        .success(true)
                        .message("Task list deleted successfully")
                        .data("Task list deleted")
                        .build()
        );
    }
}
