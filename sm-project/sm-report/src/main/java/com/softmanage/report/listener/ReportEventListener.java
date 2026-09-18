package com.softmanage.report.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.softmanage.common.constant.MQConstant;
import com.softmanage.report.entity.ExceptionStatistics;
import com.softmanage.report.entity.ProgressSnapshot;
import com.softmanage.report.mapper.ExceptionStatisticsMapper;
import com.softmanage.report.mapper.ProgressSnapshotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 监听工作流事件，更新报表统计数据。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final ProgressSnapshotMapper snapshotMapper;
    private final ExceptionStatisticsMapper exceptionMapper;

    @RabbitListener(queues = MQConstant.REPORT_QUEUE)
    public void handleWorkflowEvent(Map<String, Object> event) {
        try {
            String type = (String) event.get("type");
            Long projectId = toLong(event.get("projectId"));

            if (projectId == null) {
                log.debug("事件缺少 projectId，跳过统计更新: {}", event);
                return;
            }

            switch (type) {
                case "STATE_CHANGED" -> handleStateChanged(event, projectId);
                case "TASK_CREATED" -> handleTaskCreated(projectId);
                case "TASK_BLOCKED" -> handleTaskBlocked(projectId);
                case "TASK_UNBLOCKED" -> handleTaskUnblocked(projectId);
                default -> log.debug("忽略事件类型: {}", type);
            }

            log.info("报表服务收到事件: type={}, taskId={}", type, event.get("taskId"));
        } catch (Exception e) {
            log.error("处理报表事件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理状态变更事件 - 更新进度快照和异常统计。
     */
    private void handleStateChanged(Map<String, Object> event, Long projectId) {
        String fromState = (String) event.get("fromState");
        String toState = (String) event.get("toState");
        String actionCode = (String) event.get("actionCode");
        Object isExceptionObj = event.get("isException");
        boolean isException = isExceptionObj != null && (
                (isExceptionObj instanceof Integer i && i == 1)
                        || (isExceptionObj instanceof Boolean b && b)
                        || "1".equals(isExceptionObj.toString())
                        || "true".equalsIgnoreCase(isExceptionObj.toString())
        );

        ProgressSnapshot snapshot = getOrCreateTodaySnapshot(projectId);
        decrementStateCount(snapshot, fromState);
        incrementStateCount(snapshot, toState);
        recalculateRates(snapshot);
        snapshotMapper.updateById(snapshot);

        if (isException && actionCode != null) {
            ExceptionStatistics exStat = getOrCreateTodayExceptionStat(projectId);
            switch (actionCode) {
                case "REJECT_REVIEW" -> exStat.setReviewRejectCount(safeCount(exStat.getReviewRejectCount()) + 1);
                case "FAIL_TEST" -> exStat.setTestFailCount(safeCount(exStat.getTestFailCount()) + 1);
                case "REJECT_RELEASE" -> exStat.setReleaseRejectCount(safeCount(exStat.getReleaseRejectCount()) + 1);
                default -> {
                    return;
                }
            }
            exceptionMapper.updateById(exStat);
        }

        log.info("进度快照已更新: projectId={}, {} -> {}", projectId, fromState, toState);
    }

    /**
     * 处理任务创建事件 - 增加总任务数和 PLANNING 计数。
     */
    private void handleTaskCreated(Long projectId) {
        ProgressSnapshot snapshot = getOrCreateTodaySnapshot(projectId);
        snapshot.setTotalTasks(safeCount(snapshot.getTotalTasks()) + 1);
        snapshot.setPlanningCount(safeCount(snapshot.getPlanningCount()) + 1);
        recalculateRates(snapshot);
        snapshotMapper.updateById(snapshot);
        log.info("任务创建已统计: projectId={}", projectId);
    }

    /**
     * 处理任务阻塞事件。
     */
    private void handleTaskBlocked(Long projectId) {
        ProgressSnapshot snapshot = getOrCreateTodaySnapshot(projectId);
        snapshot.setBlockedCount(safeCount(snapshot.getBlockedCount()) + 1);
        recalculateRates(snapshot);
        snapshotMapper.updateById(snapshot);

        ExceptionStatistics exStat = getOrCreateTodayExceptionStat(projectId);
        exStat.setBlockCount(safeCount(exStat.getBlockCount()) + 1);
        exceptionMapper.updateById(exStat);
        log.info("阻塞事件已统计: projectId={}", projectId);
    }

    /**
     * 处理任务解除阻塞事件。
     */
    private void handleTaskUnblocked(Long projectId) {
        ProgressSnapshot snapshot = getOrCreateTodaySnapshot(projectId);
        if (safeCount(snapshot.getBlockedCount()) > 0) {
            snapshot.setBlockedCount(safeCount(snapshot.getBlockedCount()) - 1);
            recalculateRates(snapshot);
            snapshotMapper.updateById(snapshot);
        }
        log.info("解除阻塞已统计: projectId={}", projectId);
    }

    /**
     * 获取或创建今日进度快照（如果今天没有，则复制最近一次快照作为基线）。
     */
    private ProgressSnapshot getOrCreateTodaySnapshot(Long projectId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ProgressSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProgressSnapshot::getProjectId, projectId)
                .eq(ProgressSnapshot::getSnapshotDate, today)
                .eq(ProgressSnapshot::getSnapshotType, "DAILY");
        ProgressSnapshot snapshot = snapshotMapper.selectOne(wrapper);

        if (snapshot == null) {
            LambdaQueryWrapper<ProgressSnapshot> latestWrapper = new LambdaQueryWrapper<>();
            latestWrapper.eq(ProgressSnapshot::getProjectId, projectId)
                    .eq(ProgressSnapshot::getSnapshotType, "DAILY")
                    .orderByDesc(ProgressSnapshot::getSnapshotDate)
                    .last("LIMIT 1");
            ProgressSnapshot latest = snapshotMapper.selectOne(latestWrapper);

            snapshot = new ProgressSnapshot();
            snapshot.setProjectId(projectId);
            snapshot.setSnapshotDate(today);
            snapshot.setSnapshotType("DAILY");
            snapshot.setCreatedAt(LocalDateTime.now());

            if (latest != null) {
                snapshot.setTotalTasks(safeCount(latest.getTotalTasks()));
                snapshot.setPlanningCount(safeCount(latest.getPlanningCount()));
                snapshot.setAssignedCount(safeCount(latest.getAssignedCount()));
                snapshot.setDevCount(safeCount(latest.getDevCount()));
                snapshot.setReviewCount(safeCount(latest.getReviewCount()));
                snapshot.setTestCount(safeCount(latest.getTestCount()));
                snapshot.setReleaseCount(safeCount(latest.getReleaseCount()));
                snapshot.setReleasedCount(safeCount(latest.getReleasedCount()));
                snapshot.setCompletedCount(safeCount(latest.getCompletedCount()));
                snapshot.setBlockedCount(safeCount(latest.getBlockedCount()));
                snapshot.setCompletionRate(defaultDecimal(latest.getCompletionRate()));
                snapshot.setOnTrackRate(defaultDecimal(latest.getOnTrackRate()));
            } else {
                snapshot.setTotalTasks(0);
                snapshot.setPlanningCount(0);
                snapshot.setAssignedCount(0);
                snapshot.setDevCount(0);
                snapshot.setReviewCount(0);
                snapshot.setTestCount(0);
                snapshot.setReleaseCount(0);
                snapshot.setReleasedCount(0);
                snapshot.setCompletedCount(0);
                snapshot.setBlockedCount(0);
                snapshot.setCompletionRate(BigDecimal.ZERO);
                snapshot.setOnTrackRate(BigDecimal.ZERO);
            }
            snapshotMapper.insert(snapshot);
        }

        return snapshot;
    }

    /**
     * 获取或创建今日异常统计。
     */
    private ExceptionStatistics getOrCreateTodayExceptionStat(Long projectId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ExceptionStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExceptionStatistics::getProjectId, projectId)
                .eq(ExceptionStatistics::getStatDate, today);
        ExceptionStatistics stat = exceptionMapper.selectOne(wrapper);

        if (stat == null) {
            stat = new ExceptionStatistics();
            stat.setProjectId(projectId);
            stat.setStatDate(today);
            stat.setStatPeriod("DAILY");
            stat.setReviewRejectCount(0);
            stat.setTestFailCount(0);
            stat.setReleaseRejectCount(0);
            stat.setBlockCount(0);
            stat.setAvgFixHours(BigDecimal.ZERO);
            stat.setAvgBlockHours(BigDecimal.ZERO);
            stat.setCreatedAt(LocalDateTime.now());
            exceptionMapper.insert(stat);
        }

        return stat;
    }

    private void incrementStateCount(ProgressSnapshot snapshot, String state) {
        if (state == null) {
            return;
        }
        switch (state) {
            case "PLANNING" -> snapshot.setPlanningCount(safeCount(snapshot.getPlanningCount()) + 1);
            case "ASSIGNED" -> snapshot.setAssignedCount(safeCount(snapshot.getAssignedCount()) + 1);
            case "IN_DEVELOPMENT" -> snapshot.setDevCount(safeCount(snapshot.getDevCount()) + 1);
            case "PENDING_REVIEW" -> snapshot.setReviewCount(safeCount(snapshot.getReviewCount()) + 1);
            case "IN_TEST" -> snapshot.setTestCount(safeCount(snapshot.getTestCount()) + 1);
            case "PENDING_RELEASE" -> snapshot.setReleaseCount(safeCount(snapshot.getReleaseCount()) + 1);
            case "RELEASED" -> snapshot.setReleasedCount(safeCount(snapshot.getReleasedCount()) + 1);
            case "COMPLETED" -> snapshot.setCompletedCount(safeCount(snapshot.getCompletedCount()) + 1);
        }
    }

    private void decrementStateCount(ProgressSnapshot snapshot, String state) {
        if (state == null) {
            return;
        }
        switch (state) {
            case "PLANNING" -> snapshot.setPlanningCount(Math.max(0, safeCount(snapshot.getPlanningCount()) - 1));
            case "ASSIGNED" -> snapshot.setAssignedCount(Math.max(0, safeCount(snapshot.getAssignedCount()) - 1));
            case "IN_DEVELOPMENT" -> snapshot.setDevCount(Math.max(0, safeCount(snapshot.getDevCount()) - 1));
            case "PENDING_REVIEW" -> snapshot.setReviewCount(Math.max(0, safeCount(snapshot.getReviewCount()) - 1));
            case "IN_TEST" -> snapshot.setTestCount(Math.max(0, safeCount(snapshot.getTestCount()) - 1));
            case "PENDING_RELEASE" -> snapshot.setReleaseCount(Math.max(0, safeCount(snapshot.getReleaseCount()) - 1));
            case "RELEASED" -> snapshot.setReleasedCount(Math.max(0, safeCount(snapshot.getReleasedCount()) - 1));
            case "COMPLETED" -> snapshot.setCompletedCount(Math.max(0, safeCount(snapshot.getCompletedCount()) - 1));
        }
    }

    private void recalculateRates(ProgressSnapshot snapshot) {
        int total = safeCount(snapshot.getTotalTasks());
        int completed = safeCount(snapshot.getCompletedCount());
        int blocked = safeCount(snapshot.getBlockedCount());

        if (total > 0) {
            snapshot.setCompletionRate(BigDecimal.valueOf(completed)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));

            int onTrack = Math.max(0, total - blocked);
            snapshot.setOnTrackRate(BigDecimal.valueOf(onTrack)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
        } else {
            snapshot.setCompletionRate(BigDecimal.ZERO);
            snapshot.setOnTrackRate(BigDecimal.ZERO);
        }
    }

    private Integer safeCount(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long l) return l;
        if (obj instanceof Integer i) return i.longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

