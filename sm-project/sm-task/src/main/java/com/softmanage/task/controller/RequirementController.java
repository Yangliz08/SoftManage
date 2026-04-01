package com.softmanage.task.controller;

import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.result.Result;
import com.softmanage.task.entity.Requirement;
import com.softmanage.task.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping
    public Result<Requirement> create(@RequestBody Requirement req,
                                      @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(requirementService.createRequirement(req, userId));
    }

    @GetMapping("/{id}")
    public Result<Requirement> get(@PathVariable Long id) {
        return Result.success(requirementService.getRequirement(id));
    }

    @PutMapping("/{id}")
    public Result<Requirement> update(@PathVariable Long id, @RequestBody Requirement update) {
        return Result.success(requirementService.updateRequirement(id, update));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        requirementService.deleteRequirement(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResponse<Requirement>> list(
            @RequestParam(required = false) Long projectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(requirementService.listRequirements(projectId, page, size));
    }
}

