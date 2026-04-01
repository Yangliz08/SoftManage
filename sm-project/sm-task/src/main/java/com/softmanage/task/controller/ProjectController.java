package com.softmanage.task.controller;

import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.result.Result;
import com.softmanage.task.entity.Project;
import com.softmanage.task.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public Result<Project> createProject(@RequestBody Project project) {
        return Result.success(projectService.createProject(project));
    }

    @GetMapping("/{id}")
    public Result<Project> getProject(@PathVariable Long id) {
        return Result.success(projectService.getProject(id));
    }

    @PutMapping("/{id}")
    public Result<Project> updateProject(@PathVariable Long id, @RequestBody Project update) {
        return Result.success(projectService.updateProject(id, update));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResponse<Project>> listProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(projectService.listProjects(page, size, keyword));
    }

    @GetMapping("/my")
    public Result<List<Project>> getMyProjects(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(projectService.getProjectsByManager(userId));
    }
}

