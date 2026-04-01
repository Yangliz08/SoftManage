package com.softmanage.task.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softmanage.common.utils.RedisUtil;
import com.softmanage.task.dto.TaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务 - 仅用于看板数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisUtil redisUtil;
    private static final String BOARD_DATA_KEY = "board:data:project:";
    private static final String TASK_STATUS_KEY = "task:status:";
    private static final long BOARD_TTL_MINUTES = 5;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * 获取看板缓存
     */
    public List<TaskVO> getBoardData(Long projectId) {
        try {
            String json = redisUtil.get(BOARD_DATA_KEY + projectId);
            if (json != null) {
                log.debug("看板缓存命中: projectId={}", projectId);
                return objectMapper.readValue(json, new TypeReference<List<TaskVO>>() {});
            }
        } catch (Exception e) {
            log.warn("读取看板缓存失败: {}", e.getMessage());
        }
        log.debug("看板缓存未命中: projectId={}", projectId);
        return null;
    }

    /**
     * 设置看板缓存 (5分钟TTL)
     */
    public void setBoardData(Long projectId, List<TaskVO> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisUtil.set(BOARD_DATA_KEY + projectId, json, BOARD_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("看板数据已缓存: projectId={}, count={}", projectId, data.size());
        } catch (Exception e) {
            log.warn("写入看板缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 清除看板缓存
     */
    public void invalidateBoardData(Long projectId) {
        redisUtil.delete(BOARD_DATA_KEY + projectId);
        log.debug("看板缓存已清除: projectId={}", projectId);
    }

    /**
     * 缓存任务状态
     */
    public void setTaskStatus(Long taskId, String status) {
        redisUtil.set(TASK_STATUS_KEY + taskId, status, BOARD_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 获取任务状态缓存
     */
    public String getTaskStatus(Long taskId) {
        return redisUtil.get(TASK_STATUS_KEY + taskId);
    }

    /**
     * 清除任务状态缓存
     */
    public void invalidateTaskStatus(Long taskId) {
        redisUtil.delete(TASK_STATUS_KEY + taskId);
    }
}

