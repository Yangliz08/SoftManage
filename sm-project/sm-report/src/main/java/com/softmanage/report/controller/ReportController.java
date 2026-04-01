package com.softmanage.report.controller;

import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.result.Result;
import com.softmanage.report.entity.*;
import com.softmanage.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard(@RequestParam Long projectId) {
        return Result.success(reportService.getDashboard(projectId));
    }

    @GetMapping("/progress/trend")
    public Result<List<ProgressSnapshot>> getProgressTrend(
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "DAILY") String period,
            @RequestParam(defaultValue = "30") int days) {
        return Result.success(reportService.getProgressTrend(projectId, period, days));
    }

    @GetMapping("/workload/team")
    public Result<List<TeamWorkload>> getTeamWorkload(
            @RequestParam Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(reportService.getTeamWorkload(projectId, startDate, endDate));
    }

    @GetMapping("/workload/user")
    public Result<List<TeamWorkload>> getUserWorkload(
            @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId,
            @RequestParam(required = false) Long projectId) {
        return Result.success(reportService.getUserWorkload(userId, projectId));
    }

    @GetMapping("/exceptions/trend")
    public Result<List<ExceptionStatistics>> getExceptionTrend(
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "30") int days) {
        return Result.success(reportService.getExceptionTrend(projectId, days));
    }

    @GetMapping("/gantt")
    public Result<List<GanttTask>> getGanttData(@RequestParam Long projectId) {
        return Result.success(reportService.getGanttData(projectId));
    }
}

