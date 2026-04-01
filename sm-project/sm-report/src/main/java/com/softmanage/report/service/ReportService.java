package com.softmanage.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.softmanage.report.entity.*;
import com.softmanage.report.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProgressSnapshotMapper snapshotMapper;
    private final TeamWorkloadMapper workloadMapper;
    private final ExceptionStatisticsMapper exceptionMapper;
    private final GanttTaskMapper ganttMapper;

    /**
     * 获取仪表板数据
     */
    public Map<String, Object> getDashboard(Long projectId) {
        Map<String, Object> dashboard = new HashMap<>();

        // 最新进度快照
        LambdaQueryWrapper<ProgressSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProgressSnapshot::getProjectId, projectId)
                .orderByDesc(ProgressSnapshot::getSnapshotDate)
                .last("LIMIT 1");
        ProgressSnapshot latest = snapshotMapper.selectOne(wrapper);

        if (latest != null) {
            dashboard.put("totalTasks", latest.getTotalTasks());
            dashboard.put("completedCount", latest.getCompletedCount());
            dashboard.put("devCount", latest.getDevCount());
            dashboard.put("testCount", latest.getTestCount());
            dashboard.put("blockedCount", latest.getBlockedCount());
            dashboard.put("completionRate", latest.getCompletionRate());
            dashboard.put("onTrackRate", latest.getOnTrackRate());

            // 各状态分布
            Map<String, Integer> stateDistribution = new LinkedHashMap<>();
            stateDistribution.put("PLANNING", latest.getPlanningCount());
            stateDistribution.put("ASSIGNED", latest.getAssignedCount());
            stateDistribution.put("IN_DEVELOPMENT", latest.getDevCount());
            stateDistribution.put("PENDING_REVIEW", latest.getReviewCount());
            stateDistribution.put("IN_TEST", latest.getTestCount());
            stateDistribution.put("PENDING_RELEASE", latest.getReleaseCount());
            stateDistribution.put("RELEASED", latest.getReleasedCount());
            stateDistribution.put("COMPLETED", latest.getCompletedCount());
            dashboard.put("stateDistribution", stateDistribution);
        }

        // 最新异常统计
        LambdaQueryWrapper<ExceptionStatistics> exWrapper = new LambdaQueryWrapper<>();
        exWrapper.eq(ExceptionStatistics::getProjectId, projectId)
                .orderByDesc(ExceptionStatistics::getStatDate)
                .last("LIMIT 1");
        ExceptionStatistics exStat = exceptionMapper.selectOne(exWrapper);
        if (exStat != null) {
            dashboard.put("reviewRejectCount", exStat.getReviewRejectCount());
            dashboard.put("testFailCount", exStat.getTestFailCount());
            dashboard.put("blockCount", exStat.getBlockCount());
            dashboard.put("avgFixHours", exStat.getAvgFixHours());
        }

        return dashboard;
    }

    /**
     * 获取进度趋势数据
     */
    public List<ProgressSnapshot> getProgressTrend(Long projectId, String period, int days) {
        LambdaQueryWrapper<ProgressSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProgressSnapshot::getProjectId, projectId)
                .eq(ProgressSnapshot::getSnapshotType, period)
                .ge(ProgressSnapshot::getSnapshotDate, LocalDate.now().minusDays(days))
                .orderByAsc(ProgressSnapshot::getSnapshotDate);
        return snapshotMapper.selectList(wrapper);
    }

    /**
     * 获取团队工作量
     */
    public List<TeamWorkload> getTeamWorkload(Long projectId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<TeamWorkload> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamWorkload::getProjectId, projectId)
                .ge(TeamWorkload::getStatDate, startDate)
                .le(TeamWorkload::getStatDate, endDate)
                .orderByAsc(TeamWorkload::getStatDate);
        return workloadMapper.selectList(wrapper);
    }

    /**
     * 获取个人工作量
     */
    public List<TeamWorkload> getUserWorkload(Long userId, Long projectId) {
        LambdaQueryWrapper<TeamWorkload> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamWorkload::getUserId, userId);
        if (projectId != null) wrapper.eq(TeamWorkload::getProjectId, projectId);
        wrapper.orderByDesc(TeamWorkload::getStatDate).last("LIMIT 30");
        return workloadMapper.selectList(wrapper);
    }

    /**
     * 获取异常统计趋势
     */
    public List<ExceptionStatistics> getExceptionTrend(Long projectId, int days) {
        LambdaQueryWrapper<ExceptionStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExceptionStatistics::getProjectId, projectId)
                .ge(ExceptionStatistics::getStatDate, LocalDate.now().minusDays(days))
                .orderByAsc(ExceptionStatistics::getStatDate);
        return exceptionMapper.selectList(wrapper);
    }

    /**
     * 获取甘特图数据
     */
    public List<GanttTask> getGanttData(Long projectId) {
        LambdaQueryWrapper<GanttTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GanttTask::getProjectId, projectId)
                .orderByAsc(GanttTask::getPlannedStart);
        return ganttMapper.selectList(wrapper);
    }
}

