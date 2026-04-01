package com.softmanage.task.controller;

import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.result.Result;
import com.softmanage.task.dto.*;
import com.softmanage.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Result<TaskVO> createTask(@Valid @RequestBody TaskCreateRequest request,
                                     @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(taskService.createTask(request, userId));
    }

    @GetMapping("/{taskId}")
    public Result<TaskVO> getTaskDetail(@PathVariable Long taskId) {
        return Result.success(taskService.getTaskDetail(taskId));
    }

    @PutMapping("/{taskId}")
    public Result<TaskVO> updateTask(@PathVariable Long taskId,
                                     @RequestBody TaskUpdateRequest request) {
        return Result.success(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public Result<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return Result.success();
    }

    @PostMapping("/{taskId}/assign")
    public Result<TaskVO> assignTask(@PathVariable Long taskId,
                                     @RequestBody Map<String, Long> body,
                                     @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(taskService.assignTask(taskId, body.get("assigneeId"), userId));
    }

    @PostMapping("/{taskId}/start-dev")
    public Result<TaskVO> startDevelopment(@PathVariable Long taskId,
                                           @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(taskService.startDevelopment(taskId, userId));
    }

    @PostMapping("/{taskId}/submit-review")
    public Result<TaskVO> submitForReview(@PathVariable Long taskId,
                                          @RequestBody Map<String, String> body,
                                          @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(taskService.submitForReview(taskId, body.get("codeUrl"), userId));
    }

    @GetMapping("/board/data")
    public Result<List<TaskVO>> getBoardData(@RequestParam Long projectId) {
        return Result.success(taskService.getBoardData(projectId));
    }

    @GetMapping("/list")
    public Result<PageResponse<TaskVO>> getTaskList(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(taskService.getTaskList(projectId, state, assigneeId, page, size));
    }

    @GetMapping("/my-tasks")
    public Result<List<TaskVO>> getMyTasks(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(taskService.getMyTasks(userId));
    }

    @GetMapping("/{taskId}/status")
    public Result<String> getTaskStatus(@PathVariable Long taskId) {
        return Result.success(taskService.getTaskStatus(taskId));
    }
}

