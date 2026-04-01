package com.softmanage.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.exception.BusinessException;
import com.softmanage.common.result.ResultCode;
import com.softmanage.task.entity.Project;
import com.softmanage.task.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;

    public Project createProject(Project project) {
        projectMapper.insert(project);
        return project;
    }

    public Project getProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        return project;
    }

    public Project updateProject(Long id, Project update) {
        Project project = projectMapper.selectById(id);
        if (project == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (update.getProjectName() != null) project.setProjectName(update.getProjectName());
        if (update.getDescription() != null) project.setDescription(update.getDescription());
        if (update.getStatus() != null) project.setStatus(update.getStatus());
        if (update.getStartDate() != null) project.setStartDate(update.getStartDate());
        if (update.getEndDate() != null) project.setEndDate(update.getEndDate());
        projectMapper.updateById(project);
        return project;
    }

    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
    }

    public PageResponse<Project> listProjects(int page, int size, String keyword) {
        Page<Project> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Project::getProjectName, keyword);
        }
        wrapper.orderByDesc(Project::getCreatedAt);
        Page<Project> result = projectMapper.selectPage(pageParam, wrapper);
        return PageResponse.of(result.getRecords(), result.getTotal(), page, size);
    }

    public List<Project> getProjectsByManager(Long managerId) {
        return projectMapper.selectList(new LambdaQueryWrapper<Project>().eq(Project::getManagerId, managerId));
    }
}

