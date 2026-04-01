-- ============================================================
-- Workflow Service 工作流数据库 (sm_workflow) ⭐ 核心
-- ============================================================
USE sm_workflow;

-- -----------------------------------------------------------
-- 1. 工作流定义表 (可配置的流程模板)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS workflow_definition (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '流程定义ID',
    workflow_code   VARCHAR(50)     NOT NULL                 COMMENT '流程编码',
    workflow_name   VARCHAR(200)    NOT NULL                 COMMENT '流程名称',
    description     TEXT            DEFAULT NULL             COMMENT '流程描述',
    definition_json TEXT            NOT NULL                 COMMENT '流程定义JSON（包含节点和转移规则）',
    version         INT             NOT NULL DEFAULT 1       COMMENT '版本号',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, INACTIVE, DEPRECATED',
    created_by      BIGINT          NOT NULL                 COMMENT '创建人',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_workflow_code_version (workflow_code, version),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流定义表';

-- -----------------------------------------------------------
-- 2. 状态转移规则表 ⭐⭐⭐
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS state_transition_rule (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '规则ID',
    workflow_id     BIGINT          NOT NULL                 COMMENT '所属流程定义ID',
    from_state      VARCHAR(30)     NOT NULL                 COMMENT '源状态',
    to_state        VARCHAR(30)     NOT NULL                 COMMENT '目标状态',
    action_name     VARCHAR(50)     NOT NULL                 COMMENT '动作名称',
    action_code     VARCHAR(50)     NOT NULL                 COMMENT '动作编码',
    required_role   VARCHAR(20)     NOT NULL                 COMMENT '需要的角色: PM, DEV, TEST, ADMIN',
    description     VARCHAR(500)    DEFAULT NULL             COMMENT '描述',
    is_exception    TINYINT         NOT NULL DEFAULT 0       COMMENT '是否异常流: 0=正常, 1=异常',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_from_to_action (workflow_id, from_state, to_state, action_code),
    KEY idx_workflow_id (workflow_id),
    KEY idx_from_state (from_state),
    KEY idx_to_state (to_state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='状态转移规则表';

-- -----------------------------------------------------------
-- 3. 任务状态快照表（每次状态变更的记录）⭐⭐⭐
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_state_history (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    task_id         BIGINT          NOT NULL                 COMMENT '任务ID',
    from_state      VARCHAR(30)     NOT NULL                 COMMENT '变更前状态',
    to_state        VARCHAR(30)     NOT NULL                 COMMENT '变更后状态',
    action_code     VARCHAR(50)     NOT NULL                 COMMENT '触发动作',
    operator_id     BIGINT          NOT NULL                 COMMENT '操作人ID',
    operator_role   VARCHAR(20)     NOT NULL                 COMMENT '操作人角色',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注（如驳回原因）',
    duration_minutes INT            DEFAULT NULL             COMMENT '在前状态停留分钟数',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_from_state (from_state),
    KEY idx_to_state (to_state),
    KEY idx_operator_id (operator_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务状态变更历史表';

-- -----------------------------------------------------------
-- 4. 任务阻塞记录表 ⭐⭐⭐
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_blockage (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '阻塞记录ID',
    task_id         BIGINT          NOT NULL                 COMMENT '任务ID',
    previous_state  VARCHAR(30)     NOT NULL                 COMMENT '阻塞前的状态 ⭐ 用于恢复',
    block_reason    VARCHAR(500)    NOT NULL                 COMMENT '阻塞原因',
    block_category  VARCHAR(50)     NOT NULL                 COMMENT '阻塞分类: ENVIRONMENT, DEPENDENCY, RESOURCE, REQUIREMENT, OTHER',
    blocked_by      BIGINT          NOT NULL                 COMMENT '标记阻塞的人',
    resolved_by     BIGINT          DEFAULT NULL             COMMENT '解除阻塞的人',
    resolution      VARCHAR(500)    DEFAULT NULL             COMMENT '解决方案',
    status          VARCHAR(20)     NOT NULL DEFAULT 'BLOCKED' COMMENT '状态: BLOCKED, RESOLVED',
    blocked_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阻塞时间',
    resolved_at     DATETIME        DEFAULT NULL             COMMENT '解除时间',
    duration_hours  DECIMAL(10,2)   DEFAULT NULL             COMMENT '阻塞持续时间(小时)',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_status (status),
    KEY idx_block_category (block_category),
    KEY idx_blocked_at (blocked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务阻塞记录表';

-- -----------------------------------------------------------
-- 5. 任务异常记录表 (评审驳回/测试失败/发布拒绝)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_exception (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '异常ID',
    task_id         BIGINT          NOT NULL                 COMMENT '任务ID',
    exception_type  VARCHAR(30)     NOT NULL                 COMMENT '异常类型: REVIEW_REJECT, TEST_FAIL, RELEASE_REJECT',
    from_state      VARCHAR(30)     NOT NULL                 COMMENT '异常发生时的状态',
    to_state        VARCHAR(30)     NOT NULL                 COMMENT '回退到的状态',
    reason          TEXT            NOT NULL                 COMMENT '异常原因',
    operator_id     BIGINT          NOT NULL                 COMMENT '操作人ID',
    fix_task_id     BIGINT          DEFAULT NULL             COMMENT '关联的修复任务ID',
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN'  COMMENT '状态: OPEN, FIXING, FIXED, CLOSED',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at     DATETIME        DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_exception_type (exception_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务异常记录表';

-- -----------------------------------------------------------
-- 6. 闭环验证链表 ⭐
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS closure_chain (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '闭环链ID',
    task_id         BIGINT          NOT NULL                 COMMENT '原始任务ID',
    exception_id    BIGINT          NOT NULL                 COMMENT '关联异常ID',
    fix_task_id     BIGINT          DEFAULT NULL             COMMENT '修复任务ID',
    chain_status    VARCHAR(20)     NOT NULL DEFAULT 'INITIATED' COMMENT '链状态: INITIATED, FIX_DONE, REVIEW_PASSED, TEST_PASSED, CLOSED',
    step1_fix_done       TINYINT   NOT NULL DEFAULT 0       COMMENT '步骤1: 修复完成',
    step1_time           DATETIME  DEFAULT NULL,
    step2_review_passed  TINYINT   NOT NULL DEFAULT 0       COMMENT '步骤2: 评审通过',
    step2_time           DATETIME  DEFAULT NULL,
    step3_test_passed    TINYINT   NOT NULL DEFAULT 0       COMMENT '步骤3: 测试通过',
    step3_time           DATETIME  DEFAULT NULL,
    step4_closed         TINYINT   NOT NULL DEFAULT 0       COMMENT '步骤4: 闭环完成',
    step4_time           DATETIME  DEFAULT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_exception_id (exception_id),
    KEY idx_chain_status (chain_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='闭环验证链表';

-- -----------------------------------------------------------
-- 初始数据: 默认工作流定义
-- -----------------------------------------------------------
INSERT INTO workflow_definition (workflow_code, workflow_name, description, definition_json, version, created_by) VALUES
('DEFAULT_WORKFLOW', '默认软件开发流程', '标准的软件开发全流程：需求→开发→评审→测试→发布→完成',
'{
  "states": [
    {"code": "PLANNING", "name": "需求管理", "type": "START"},
    {"code": "ASSIGNED", "name": "任务准备", "type": "NORMAL"},
    {"code": "IN_DEVELOPMENT", "name": "开发中", "type": "NORMAL"},
    {"code": "PENDING_REVIEW", "name": "待评审", "type": "NORMAL"},
    {"code": "IN_TEST", "name": "测试中", "type": "NORMAL"},
    {"code": "PENDING_RELEASE", "name": "待发布", "type": "NORMAL"},
    {"code": "RELEASED", "name": "已上线", "type": "NORMAL"},
    {"code": "COMPLETED", "name": "已完成", "type": "END"},
    {"code": "BLOCKED", "name": "已阻塞", "type": "EXCEPTION"}
  ]
}', 1, 1);

-- -----------------------------------------------------------
-- 初始数据: 状态转移规则（主干流程 + 异常流）
-- -----------------------------------------------------------
INSERT INTO state_transition_rule (workflow_id, from_state, to_state, action_name, action_code, required_role, is_exception, sort_order) VALUES
-- 主干流程
(1, 'PLANNING',        'ASSIGNED',        '分配任务',   'ASSIGN_TASK',       'PM',    0, 1),
(1, 'ASSIGNED',        'IN_DEVELOPMENT',  '开始开发',   'START_DEV',         'DEV',   0, 2),
(1, 'IN_DEVELOPMENT',  'PENDING_REVIEW',  '提交评审',   'SUBMIT_REVIEW',     'DEV',   0, 3),
(1, 'PENDING_REVIEW',  'IN_TEST',         '评审通过',   'APPROVE_REVIEW',    'PM',    0, 4),
(1, 'IN_TEST',         'PENDING_RELEASE', '测试通过',   'PASS_TEST',         'TEST',  0, 5),
(1, 'PENDING_RELEASE', 'RELEASED',        '审批发布',   'APPROVE_RELEASE',   'ADMIN', 0, 6),
(1, 'RELEASED',        'COMPLETED',       '确认完成',   'CONFIRM_COMPLETE',  'PM',    0, 7),

-- 异常流: 评审驳回 → 回到开发
(1, 'PENDING_REVIEW',  'IN_DEVELOPMENT',  '评审驳回',   'REJECT_REVIEW',     'PM',    1, 10),
-- 异常流: 测试失败 → 回到开发
(1, 'IN_TEST',         'IN_DEVELOPMENT',  '测试失败',   'FAIL_TEST',         'TEST',  1, 11),
-- 异常流: 发布拒绝 → 回到测试
(1, 'PENDING_RELEASE', 'IN_TEST',         '发布拒绝',   'REJECT_RELEASE',    'ADMIN', 1, 12),

-- 阻塞相关
(1, 'IN_DEVELOPMENT',  'BLOCKED',         '标记阻塞',   'BLOCK_TASK',        'DEV',   1, 20),
(1, 'PENDING_REVIEW',  'BLOCKED',         '标记阻塞',   'BLOCK_TASK',        'PM',    1, 21),
(1, 'IN_TEST',         'BLOCKED',         '标记阻塞',   'BLOCK_TASK',        'TEST',  1, 22);

