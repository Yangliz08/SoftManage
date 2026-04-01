package com.softmanage.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softmanage.common.constant.MQConstant;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.exception.BusinessException;
import com.softmanage.common.result.ResultCode;
import com.softmanage.task.dto.*;
import com.softmanage.task.entity.Task;
import com.softmanage.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final CacheService cacheService;
    private final RabbitTemplate rabbitTemplate;

    private static final AtomicLong TASK_SEQ = new AtomicLong(System.currentTimeMillis() % 10000);

    /**
     * 创建任务
     */
    @Transactional
    public TaskVO createTask(TaskCreateRequest request, Long creatorId) {
        Task task = new Task();
        task.setTaskCode("TASK-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + TASK_SEQ.incrementAndGet());
        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setProjectId(request.getProjectId());
        task.setRequirementId(request.getRequirementId());
        task.setParentTaskId(request.getParentTaskId());
        task.setTaskType(request.getTaskType());
        task.setCurrentState("PLANNING");
        task.setPriority(request.getPriority());
        task.setProgress(0);
        task.setCreatorId(creatorId);
        task.setAssigneeId(request.getAssigneeId());
        task.setReviewerId(request.getReviewerId());
        task.setTesterId(request.getTesterId());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        task.setIsBlocked(0);
        taskMapper.insert(task);

        // 清除看板缓存
        cacheService.invalidateBoardData(task.getProjectId());

        // 发送消息到 MQ
        Map<String, Object> event = new HashMap<>();
        event.put("type", "TASK_CREATED");
        event.put("taskId", task.getId());
        event.put("taskCode", task.getTaskCode());
        event.put("taskName", task.getTaskName());
        event.put("projectId", task.getProjectId());
        event.put("creatorId", creatorId);
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.WORKFLOW_EXCHANGE, MQConstant.TASK_CREATED_KEY, event);

        return toVO(task);
    }

    /**
     * 获取任务详情
     */
    public TaskVO getTaskDetail(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        return toVO(task);
    }

    /**
     * 更新任务
     */
    @Transactional
    public TaskVO updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        if (request.getTaskName() != null) task.setTaskName(request.getTaskName());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getAssigneeId() != null) task.setAssigneeId(request.getAssigneeId());
        if (request.getReviewerId() != null) task.setReviewerId(request.getReviewerId());
        if (request.getTesterId() != null) task.setTesterId(request.getTesterId());
        if (request.getEstimatedHours() != null) task.setEstimatedHours(request.getEstimatedHours());
        if (request.getActualHours() != null) task.setActualHours(request.getActualHours());
        if (request.getProgress() != null) task.setProgress(request.getProgress());
        if (request.getStartDate() != null) task.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) task.setEndDate(request.getEndDate());
        if (request.getCodeUrl() != null) task.setCodeUrl(request.getCodeUrl());

        taskMapper.updateById(task);
        cacheService.invalidateBoardData(task.getProjectId());
        cacheService.invalidateTaskStatus(taskId);

        return toVO(task);
    }

    /**
     * 删除任务
     */
    public void deleteTask(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        taskMapper.deleteById(taskId);
        cacheService.invalidateBoardData(task.getProjectId());
    }

    /**
     * 分配任务
     */
    @Transactional
    public TaskVO assignTask(Long taskId, Long assigneeId, Long operatorId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        task.setAssigneeId(assigneeId);
        if ("PLANNING".equals(task.getCurrentState())) {
            task.setPreviousState(task.getCurrentState());
            task.setCurrentState("ASSIGNED");
        }
        taskMapper.updateById(task);

        cacheService.invalidateBoardData(task.getProjectId());

        // 发送分配事件
        Map<String, Object> event = new HashMap<>();
        event.put("type", "TASK_ASSIGNED");
        event.put("taskId", task.getId());
        event.put("taskCode", task.getTaskCode());
        event.put("taskName", task.getTaskName());
        event.put("assigneeId", assigneeId);
        event.put("operatorId", operatorId);
        event.put("projectId", task.getProjectId());
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.NOTIFICATION_EXCHANGE, MQConstant.TASK_ASSIGNED_KEY, event);

        return toVO(task);
    }

    /**
     * 获取看板数据 (带Redis缓存)
     */
    public List<TaskVO> getBoardData(Long projectId) {
        // 先查缓存
        List<TaskVO> cached = cacheService.getBoardData(projectId);
        if (cached != null) {
            return cached;
        }

        // 缓存未命中，查数据库
        List<Task> tasks = taskMapper.findBoardDataByProject(projectId);
        List<TaskVO> result = tasks.stream().map(this::toVO).collect(Collectors.toList());

        // 写入缓存
        cacheService.setBoardData(projectId, result);

        return result;
    }

    /**
     * 获取任务列表（分页）
     */
    public PageResponse<TaskVO> getTaskList(Long projectId, String state, Long assigneeId, int page, int size) {
        Page<Task> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) wrapper.eq(Task::getProjectId, projectId);
        if (state != null) wrapper.eq(Task::getCurrentState, state);
        if (assigneeId != null) wrapper.eq(Task::getAssigneeId, assigneeId);
        wrapper.orderByDesc(Task::getCreatedAt);

        Page<Task> result = taskMapper.selectPage(pageParam, wrapper);
        List<TaskVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResponse.of(records, result.getTotal(), page, size);
    }

    /**
     * 获取我的任务
     */
    public List<TaskVO> getMyTasks(Long userId) {
        List<Task> tasks = taskMapper.findByAssigneeId(userId);
        return tasks.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 获取任务状态（带缓存）
     */
    public String getTaskStatus(Long taskId) {
        String cached = cacheService.getTaskStatus(taskId);
        if (cached != null) return cached;

        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new BusinessException(ResultCode.TASK_NOT_FOUND);

        cacheService.setTaskStatus(taskId, task.getCurrentState());
        return task.getCurrentState();
    }

    /**
     * 提交评审
     */
    @Transactional
    public TaskVO submitForReview(Long taskId, String codeUrl, Long operatorId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        if (!"IN_DEVELOPMENT".equals(task.getCurrentState())) {
            throw new BusinessException(ResultCode.TASK_STATE_ERROR);
        }

        task.setCodeUrl(codeUrl);
        task.setPreviousState(task.getCurrentState());
        task.setCurrentState("PENDING_REVIEW");
        taskMapper.updateById(task);

        cacheService.invalidateBoardData(task.getProjectId());
        cacheService.invalidateTaskStatus(taskId);

        Map<String, Object> event = new HashMap<>();
        event.put("type", "STATE_CHANGED");
        event.put("taskId", task.getId());
        event.put("taskCode", task.getTaskCode());
        event.put("taskName", task.getTaskName());
        event.put("fromState", "IN_DEVELOPMENT");
        event.put("toState", "PENDING_REVIEW");
        event.put("actionCode", "SUBMIT_REVIEW");
        event.put("operatorId", operatorId);
        event.put("projectId", task.getProjectId());
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.WORKFLOW_EXCHANGE, MQConstant.STATE_CHANGED_KEY, event);

        return toVO(task);
    }

    /**
     * 开始开发
     */
    @Transactional
    public TaskVO startDevelopment(Long taskId, Long operatorId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        if (!"ASSIGNED".equals(task.getCurrentState())) {
            throw new BusinessException(ResultCode.TASK_STATE_ERROR);
        }

        task.setPreviousState(task.getCurrentState());
        task.setCurrentState("IN_DEVELOPMENT");
        task.setActualStart(LocalDate.now());
        taskMapper.updateById(task);

        cacheService.invalidateBoardData(task.getProjectId());
        cacheService.invalidateTaskStatus(taskId);

        Map<String, Object> event = new HashMap<>();
        event.put("type", "STATE_CHANGED");
        event.put("taskId", task.getId());
        event.put("taskCode", task.getTaskCode());
        event.put("taskName", task.getTaskName());
        event.put("fromState", "ASSIGNED");
        event.put("toState", "IN_DEVELOPMENT");
        event.put("actionCode", "START_DEV");
        event.put("operatorId", operatorId);
        event.put("projectId", task.getProjectId());
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.WORKFLOW_EXCHANGE, MQConstant.STATE_CHANGED_KEY, event);

        return toVO(task);
    }

    private TaskVO toVO(Task task) {
        return TaskVO.builder()
                .id(task.getId())
                .taskCode(task.getTaskCode())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .projectId(task.getProjectId())
                .requirementId(task.getRequirementId())
                .parentTaskId(task.getParentTaskId())
                .taskType(task.getTaskType())
                .currentState(task.getCurrentState())
                .previousState(task.getPreviousState())
                .priority(task.getPriority())
                .progress(task.getProgress())
                .creatorId(task.getCreatorId())
                .assigneeId(task.getAssigneeId())
                .reviewerId(task.getReviewerId())
                .testerId(task.getTesterId())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .actualStart(task.getActualStart())
                .actualEnd(task.getActualEnd())
                .codeUrl(task.getCodeUrl())
                .isBlocked(task.getIsBlocked())
                .blockReason(task.getBlockReason())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

