-- ============================================================
-- Notification Service 通知数据库 (sm_notification)
-- ============================================================
USE sm_notification;

-- -----------------------------------------------------------
-- 1. 通知记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS notification (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL                 COMMENT '接收用户ID',
    title           VARCHAR(200)    NOT NULL                 COMMENT '通知标题',
    content         TEXT            NOT NULL                 COMMENT '通知内容',
    notify_type     VARCHAR(30)     NOT NULL                 COMMENT '类型: TASK_ASSIGNED, REVIEW_RESULT, TEST_RESULT, RELEASE_RESULT, BLOCK_ALERT, SYSTEM',
    biz_type        VARCHAR(30)     DEFAULT NULL             COMMENT '业务类型: TASK, WORKFLOW, SYSTEM',
    biz_id          BIGINT          DEFAULT NULL             COMMENT '业务ID(如任务ID)',
    level           VARCHAR(20)     NOT NULL DEFAULT 'INFO'  COMMENT '级别: INFO, WARNING, ERROR, URGENT',
    is_read         TINYINT         NOT NULL DEFAULT 0       COMMENT '是否已读: 0=未读, 1=已读',
    read_time       DATETIME        DEFAULT NULL             COMMENT '阅读时间',
    sender_id       BIGINT          DEFAULT NULL             COMMENT '发送者ID(系统通知为空)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_is_read (is_read),
    KEY idx_notify_type (notify_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

-- -----------------------------------------------------------
-- 2. 通知模板表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS notification_template (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    template_code   VARCHAR(50)     NOT NULL                 COMMENT '模板编码',
    template_name   VARCHAR(100)    NOT NULL                 COMMENT '模板名称',
    title_template  VARCHAR(200)    NOT NULL                 COMMENT '标题模板',
    content_template TEXT           NOT NULL                 COMMENT '内容模板（支持变量替换 ${varName}）',
    notify_type     VARCHAR(30)     NOT NULL                 COMMENT '通知类型',
    channel         VARCHAR(20)     NOT NULL DEFAULT 'SYSTEM' COMMENT '渠道: SYSTEM, EMAIL, DINGTALK, WECHAT',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- -----------------------------------------------------------
-- 3. 通知设置表（用户个人偏好）
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS notification_setting (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    notify_type     VARCHAR(30)     NOT NULL                 COMMENT '通知类型',
    enable_system   TINYINT         NOT NULL DEFAULT 1       COMMENT '启用系统通知',
    enable_email    TINYINT         NOT NULL DEFAULT 1       COMMENT '启用邮件通知',
    enable_dingtalk TINYINT         NOT NULL DEFAULT 0       COMMENT '启用钉钉通知',
    enable_wechat   TINYINT         NOT NULL DEFAULT 0       COMMENT '启用企微通知',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_type (user_id, notify_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知设置表';

-- -----------------------------------------------------------
-- 初始数据: 通知模板
-- -----------------------------------------------------------
INSERT INTO notification_template (template_code, template_name, title_template, content_template, notify_type, channel) VALUES
('TASK_ASSIGNED',    '任务分配通知',     '您有新任务: ${taskName}',         '${pmName} 将任务 [${taskCode}] ${taskName} 分配给您，优先级: ${priority}，请及时处理。',                  'TASK_ASSIGNED',    'SYSTEM'),
('REVIEW_APPROVED',  '评审通过通知',     '代码评审通过: ${taskName}',       '任务 [${taskCode}] ${taskName} 的代码评审已通过，将进入测试阶段。',                                    'REVIEW_RESULT',    'SYSTEM'),
('REVIEW_REJECTED',  '评审驳回通知',     '代码评审驳回: ${taskName}',       '任务 [${taskCode}] ${taskName} 的代码评审被驳回，原因: ${reason}，请修改后重新提交。',                 'REVIEW_RESULT',    'SYSTEM'),
('TEST_PASSED',      '测试通过通知',     '测试通过: ${taskName}',           '任务 [${taskCode}] ${taskName} 测试通过，将进入发布审批阶段。',                                         'TEST_RESULT',      'SYSTEM'),
('TEST_FAILED',      '测试失败通知',     '测试失败: ${taskName}',           '任务 [${taskCode}] ${taskName} 测试失败，缺陷数: ${defectCount}，请及时修复。',                         'TEST_RESULT',      'SYSTEM'),
('RELEASE_APPROVED', '发布审批通过',     '发布审批通过: ${taskName}',       '任务 [${taskCode}] ${taskName} 发布审批通过，已上线。',                                                  'RELEASE_RESULT',   'SYSTEM'),
('RELEASE_REJECTED', '发布审批拒绝',     '发布审批拒绝: ${taskName}',       '任务 [${taskCode}] ${taskName} 发布审批被拒绝，原因: ${reason}。',                                      'RELEASE_RESULT',   'SYSTEM'),
('TASK_BLOCKED',     '任务阻塞提醒',     '任务被阻塞: ${taskName}',         '任务 [${taskCode}] ${taskName} 被标记为阻塞，分类: ${category}，原因: ${reason}。',                     'BLOCK_ALERT',      'SYSTEM'),
('TASK_UNBLOCKED',   '任务恢复通知',     '任务已恢复: ${taskName}',         '任务 [${taskCode}] ${taskName} 已解除阻塞，恢复到 ${recoveredState} 状态，请继续处理。',               'BLOCK_ALERT',      'SYSTEM');

