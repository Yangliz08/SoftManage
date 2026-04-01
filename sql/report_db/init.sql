-- ============================================================
-- Report Service 报表数据库 (sm_report)
-- ============================================================
USE sm_report;

-- -----------------------------------------------------------
-- 1. 进度快照表（定时生成）
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS progress_snapshot (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    project_id      BIGINT          NOT NULL                 COMMENT '项目ID',
    snapshot_date   DATE            NOT NULL                 COMMENT '快照日期',
    snapshot_type   VARCHAR(20)     NOT NULL DEFAULT 'DAILY' COMMENT '类型: HOURLY, DAILY, WEEKLY, MONTHLY',
    total_tasks     INT             NOT NULL DEFAULT 0       COMMENT '总任务数',
    planning_count  INT             NOT NULL DEFAULT 0       COMMENT '需求管理中',
    assigned_count  INT             NOT NULL DEFAULT 0       COMMENT '任务准备中',
    dev_count       INT             NOT NULL DEFAULT 0       COMMENT '开发中',
    review_count    INT             NOT NULL DEFAULT 0       COMMENT '待评审',
    test_count      INT             NOT NULL DEFAULT 0       COMMENT '测试中',
    release_count   INT             NOT NULL DEFAULT 0       COMMENT '待发布',
    released_count  INT             NOT NULL DEFAULT 0       COMMENT '已上线',
    completed_count INT             NOT NULL DEFAULT 0       COMMENT '已完成',
    blocked_count   INT             NOT NULL DEFAULT 0       COMMENT '被阻塞',
    completion_rate DECIMAL(5,2)    NOT NULL DEFAULT 0       COMMENT '完成率(%)',
    on_track_rate   DECIMAL(5,2)    NOT NULL DEFAULT 0       COMMENT '按时率(%)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_project_id (project_id),
    KEY idx_snapshot_date (snapshot_date),
    KEY idx_snapshot_type (snapshot_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='进度快照表';

-- -----------------------------------------------------------
-- 2. 团队工作量统计表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS team_workload (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    project_id      BIGINT          NOT NULL                 COMMENT '项目ID',
    stat_date       DATE            NOT NULL                 COMMENT '统计日期',
    stat_period     VARCHAR(20)     NOT NULL DEFAULT 'DAILY' COMMENT '统计周期: DAILY, WEEKLY, MONTHLY',
    assigned_tasks  INT             NOT NULL DEFAULT 0       COMMENT '分配的任务数',
    completed_tasks INT             NOT NULL DEFAULT 0       COMMENT '完成的任务数',
    in_progress_tasks INT           NOT NULL DEFAULT 0       COMMENT '进行中的任务数',
    blocked_tasks   INT             NOT NULL DEFAULT 0       COMMENT '被阻塞的任务数',
    estimated_hours DECIMAL(10,2)   DEFAULT 0                COMMENT '预估工时',
    actual_hours    DECIMAL(10,2)   DEFAULT 0                COMMENT '实际工时',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_project_id (project_id),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队工作量统计表';

-- -----------------------------------------------------------
-- 3. 异常统计表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS exception_statistics (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    project_id      BIGINT          NOT NULL                 COMMENT '项目ID',
    stat_date       DATE            NOT NULL                 COMMENT '统计日期',
    stat_period     VARCHAR(20)     NOT NULL DEFAULT 'DAILY',
    review_reject_count   INT       NOT NULL DEFAULT 0       COMMENT '评审驳回次数',
    test_fail_count       INT       NOT NULL DEFAULT 0       COMMENT '测试失败次数',
    release_reject_count  INT       NOT NULL DEFAULT 0       COMMENT '发布拒绝次数',
    block_count           INT       NOT NULL DEFAULT 0       COMMENT '阻塞次数',
    avg_fix_hours         DECIMAL(10,2) DEFAULT NULL         COMMENT '平均修复时长(小时)',
    avg_block_hours       DECIMAL(10,2) DEFAULT NULL         COMMENT '平均阻塞时长(小时)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_project_id (project_id),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='异常统计表';

-- -----------------------------------------------------------
-- 4. 甘特图任务数据表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS gantt_task (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    task_id         BIGINT          NOT NULL                 COMMENT '关联任务ID',
    project_id      BIGINT          NOT NULL                 COMMENT '项目ID',
    task_name       VARCHAR(200)    NOT NULL                 COMMENT '任务名称',
    assignee_name   VARCHAR(50)     DEFAULT NULL             COMMENT '负责人',
    planned_start   DATE            DEFAULT NULL             COMMENT '计划开始',
    planned_end     DATE            DEFAULT NULL             COMMENT '计划结束',
    actual_start    DATE            DEFAULT NULL             COMMENT '实际开始',
    actual_end      DATE            DEFAULT NULL             COMMENT '实际结束',
    progress        INT             NOT NULL DEFAULT 0       COMMENT '进度',
    dependency_ids  VARCHAR(500)    DEFAULT NULL             COMMENT '依赖任务ID列表(逗号分隔)',
    milestone       TINYINT         NOT NULL DEFAULT 0       COMMENT '是否里程碑',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='甘特图任务数据表';

-- -----------------------------------------------------------
-- 5. 报表导出记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS report_export (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    report_name     VARCHAR(200)    NOT NULL                 COMMENT '报表名称',
    report_type     VARCHAR(50)     NOT NULL                 COMMENT '类型: PROGRESS, WORKLOAD, EXCEPTION, GANTT',
    export_format   VARCHAR(20)     NOT NULL                 COMMENT '格式: EXCEL, PDF, CSV',
    file_path       VARCHAR(500)    DEFAULT NULL             COMMENT '文件路径',
    project_id      BIGINT          NOT NULL                 COMMENT '项目ID',
    operator_id     BIGINT          NOT NULL                 COMMENT '导出人',
    status          VARCHAR(20)     NOT NULL DEFAULT 'GENERATING' COMMENT '状态: GENERATING, COMPLETED, FAILED',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_project_id (project_id),
    KEY idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表导出记录表';

-- -----------------------------------------------------------
-- 初始数据: 最新进度快照（项目1）
-- -----------------------------------------------------------
INSERT INTO progress_snapshot (project_id, snapshot_date, snapshot_type, total_tasks, planning_count, assigned_count, dev_count, review_count, test_count, release_count, released_count, completed_count, blocked_count, completion_rate, on_track_rate) VALUES
(1, CURDATE() - INTERVAL 6 DAY, 'DAILY', 12, 3, 2, 2, 1, 2, 0, 1, 1, 1, 8.33, 75.00),
(1, CURDATE() - INTERVAL 5 DAY, 'DAILY', 12, 2, 2, 3, 1, 2, 0, 1, 1, 1, 8.33, 77.00),
(1, CURDATE() - INTERVAL 4 DAY, 'DAILY', 12, 2, 2, 3, 1, 2, 1, 1, 1, 1, 8.33, 79.00),
(1, CURDATE() - INTERVAL 3 DAY, 'DAILY', 12, 2, 2, 3, 1, 2, 1, 1, 1, 1, 8.33, 80.00),
(1, CURDATE() - INTERVAL 2 DAY, 'DAILY', 12, 2, 2, 3, 1, 2, 1, 1, 1, 1, 8.33, 82.00),
(1, CURDATE() - INTERVAL 1 DAY, 'DAILY', 14, 2, 2, 3, 1, 2, 1, 1, 2, 1, 14.29, 83.00),
(1, CURDATE(), 'DAILY', 14, 2, 2, 3, 1, 2, 1, 1, 2, 1, 14.29, 85.00);

-- -----------------------------------------------------------
-- 初始数据: 团队工作量（项目1，近7天）
-- -----------------------------------------------------------
INSERT INTO team_workload (user_id, project_id, stat_date, stat_period, assigned_tasks, completed_tasks, in_progress_tasks, blocked_tasks, estimated_hours, actual_hours) VALUES
(3, 1, CURDATE() - INTERVAL 6 DAY, 'DAILY', 8, 1, 5, 1, 40.00, 8.00),
(3, 1, CURDATE() - INTERVAL 5 DAY, 'DAILY', 8, 1, 5, 1, 40.00, 8.50),
(3, 1, CURDATE() - INTERVAL 4 DAY, 'DAILY', 8, 2, 5, 1, 40.00, 7.50),
(3, 1, CURDATE() - INTERVAL 3 DAY, 'DAILY', 8, 2, 5, 1, 40.00, 9.00),
(3, 1, CURDATE() - INTERVAL 2 DAY, 'DAILY', 9, 2, 5, 1, 44.00, 8.00),
(3, 1, CURDATE() - INTERVAL 1 DAY, 'DAILY', 9, 2, 5, 1, 44.00, 8.50),
(3, 1, CURDATE(), 'DAILY', 9, 2, 5, 1, 44.00, 6.00),
(4, 1, CURDATE() - INTERVAL 6 DAY, 'DAILY', 5, 0, 3, 0, 24.00, 7.00),
(4, 1, CURDATE() - INTERVAL 5 DAY, 'DAILY', 5, 0, 3, 0, 24.00, 7.50),
(4, 1, CURDATE() - INTERVAL 3 DAY, 'DAILY', 5, 0, 3, 0, 24.00, 8.00),
(4, 1, CURDATE(), 'DAILY', 5, 0, 3, 0, 24.00, 6.00);

-- -----------------------------------------------------------
-- 初始数据: 异常统计（项目1，近7天）
-- -----------------------------------------------------------
INSERT INTO exception_statistics (project_id, stat_date, stat_period, review_reject_count, test_fail_count, release_reject_count, block_count, avg_fix_hours, avg_block_hours) VALUES
(1, CURDATE() - INTERVAL 6 DAY, 'DAILY', 0, 0, 0, 0, NULL, NULL),
(1, CURDATE() - INTERVAL 5 DAY, 'DAILY', 1, 0, 0, 0, 4.00, NULL),
(1, CURDATE() - INTERVAL 4 DAY, 'DAILY', 0, 1, 0, 0, 8.00, NULL),
(1, CURDATE() - INTERVAL 3 DAY, 'DAILY', 0, 0, 0, 1, NULL, 24.00),
(1, CURDATE() - INTERVAL 2 DAY, 'DAILY', 1, 0, 0, 0, 6.00, NULL),
(1, CURDATE() - INTERVAL 1 DAY, 'DAILY', 0, 0, 0, 0, NULL, NULL),
(1, CURDATE(), 'DAILY', 0, 0, 0, 0, NULL, NULL);

-- -----------------------------------------------------------
-- 初始数据: 甘特图任务数据（项目1）
-- -----------------------------------------------------------
INSERT INTO gantt_task (task_id, project_id, task_name, assignee_name, planned_start, planned_end, actual_start, actual_end, progress, dependency_ids, milestone) VALUES
(1,  1, '数据库架构设计',               '开发李四', '2026-01-05', '2026-01-10', '2026-01-05', '2026-01-09', 100, NULL,  0),
(2,  1, 'JWT认证与登录模块开发',         '开发李四', '2026-01-08', '2026-01-15', '2026-01-08', '2026-01-14', 100, '1',   0),
(3,  1, '工作流引擎核心开发',            '开发李四', '2026-01-15', '2026-01-30', '2026-01-15', '2026-01-29', 100, '1',   0),
(4,  1, '状态转移规则管理',              '开发李四', '2026-01-25', '2026-02-10', '2026-01-25', NULL,          95, '3',   0),
(5,  1, '任务看板前后端开发',            '开发李四', '2026-01-28', '2026-02-15', '2026-01-28', NULL,          80, '1',   0),
(6,  1, 'Redis看板缓存层实现',           '开发李四', '2026-02-01', '2026-02-10', '2026-02-01', NULL,          85, '5',   0),
(7,  1, '异常闭环处理机制',              '开发李四', '2026-02-05', '2026-02-20', '2026-02-05', NULL,          90, '3',   0),
(8,  1, 'previous_state回溯恢复机制',    '开发李四', '2026-02-10', '2026-02-25', '2026-02-10', NULL,          60, '3,7', 0),
(9,  1, '甘特图数据接口开发',            '开发李四', '2026-02-12', '2026-02-28', '2026-02-12', NULL,          40, '1',   0),
(10, 1, '报表统计与导出功能',            '开发李四', '2026-02-20', '2026-03-10', NULL,         NULL,           0, '9',   0),
(11, 1, '通知服务与消息模板',            '开发李四', '2026-02-25', '2026-03-10', NULL,         NULL,           0, '1',   0);


