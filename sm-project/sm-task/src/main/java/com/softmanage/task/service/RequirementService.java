package com.softmanage.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.exception.BusinessException;
import com.softmanage.common.result.ResultCode;
import com.softmanage.task.entity.Requirement;
import com.softmanage.task.mapper.RequirementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementMapper requirementMapper;
    private static final AtomicLong REQ_SEQ = new AtomicLong(System.currentTimeMillis() % 10000);

    public Requirement createRequirement(Requirement req, Long creatorId) {
        req.setReqCode("REQ-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + REQ_SEQ.incrementAndGet());
        req.setCreatorId(creatorId);
        if (req.getStatus() == null) req.setStatus("DRAFT");
        requirementMapper.insert(req);
        return req;
    }

    public Requirement getRequirement(Long id) {
        Requirement req = requirementMapper.selectById(id);
        if (req == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        return req;
    }

    public Requirement updateRequirement(Long id, Requirement update) {
        Requirement req = requirementMapper.selectById(id);
        if (req == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (update.getReqName() != null) req.setReqName(update.getReqName());
        if (update.getDescription() != null) req.setDescription(update.getDescription());
        if (update.getPriority() != null) req.setPriority(update.getPriority());
        if (update.getStatus() != null) req.setStatus(update.getStatus());
        if (update.getAssigneeId() != null) req.setAssigneeId(update.getAssigneeId());
        requirementMapper.updateById(req);
        return req;
    }

    public void deleteRequirement(Long id) {
        requirementMapper.deleteById(id);
    }

    public PageResponse<Requirement> listRequirements(Long projectId, int page, int size) {
        Page<Requirement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) wrapper.eq(Requirement::getProjectId, projectId);
        wrapper.orderByDesc(Requirement::getCreatedAt);
        Page<Requirement> result = requirementMapper.selectPage(pageParam, wrapper);
        return PageResponse.of(result.getRecords(), result.getTotal(), page, size);
    }
}

