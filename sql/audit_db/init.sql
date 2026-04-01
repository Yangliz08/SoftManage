-- ============================================================
-- Audit Service 审计数据库 (sm_audit)
-- ============================================================
USE sm_audit;

-- -----------------------------------------------------------
-- 1. 审计日志表 ⭐
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    trace_id        VARCHAR(64)     DEFAULT NULL             COMMENT '链路追踪ID',
    service_name    VARCHAR(50)     NOT NULL                 COMMENT '服务名: auth, task, workflow, report, notification',
    module          VARCHAR(50)     NOT NULL                 COMMENT '模块名',
    action          VARCHAR(100)    NOT NULL                 COMMENT '操作动作',
    method          VARCHAR(10)     NOT NULL                 COMMENT 'HTTP方法: GET, POST, PUT, DELETE',
    request_url     VARCHAR(500)    NOT NULL                 COMMENT '请求URL',
    request_params  TEXT            DEFAULT NULL             COMMENT '请求参数(JSON)',
    response_code   INT             DEFAULT NULL             COMMENT '响应状态码',
    response_data   TEXT            DEFAULT NULL             COMMENT '响应数据(JSON, 可选)',
    user_id         BIGINT          DEFAULT NULL             COMMENT '操作用户ID',
    username        VARCHAR(50)     DEFAULT NULL             COMMENT '操作用户名',
    user_role       VARCHAR(20)     DEFAULT NULL             COMMENT '用户角色',
    ip_address      VARCHAR(50)     DEFAULT NULL             COMMENT 'IP地址',
    user_agent      VARCHAR(500)    DEFAULT NULL             COMMENT '浏览器信息',
    execution_time  BIGINT          DEFAULT NULL             COMMENT '执行耗时(毫秒)',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '结果: 0=失败, 1=成功',
    error_message   TEXT            DEFAULT NULL             COMMENT '错误信息',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_trace_id (trace_id),
    KEY idx_service_name (service_name),
    KEY idx_user_id (user_id),
    KEY idx_action (action),
    KEY idx_created_at (created_at),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- -----------------------------------------------------------
-- 2. 数据变更记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS data_change_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    audit_log_id    BIGINT          DEFAULT NULL             COMMENT '关联审计日志ID',
    table_name      VARCHAR(100)    NOT NULL                 COMMENT '表名',
    record_id       BIGINT          NOT NULL                 COMMENT '记录ID',
    change_type     VARCHAR(20)     NOT NULL                 COMMENT '变更类型: INSERT, UPDATE, DELETE',
    old_value       TEXT            DEFAULT NULL             COMMENT '变更前数据(JSON)',
    new_value       TEXT            DEFAULT NULL             COMMENT '变更后数据(JSON)',
    changed_fields  VARCHAR(500)    DEFAULT NULL             COMMENT '变更的字段列表(逗号分隔)',
    user_id         BIGINT          DEFAULT NULL             COMMENT '操作用户ID',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_audit_log_id (audit_log_id),
    KEY idx_table_name (table_name),
    KEY idx_record_id (record_id),
    KEY idx_change_type (change_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据变更记录表';

-- -----------------------------------------------------------
-- 3. 审计配置表（配置哪些操作需要记录）
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_config (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    service_name    VARCHAR(50)     NOT NULL                 COMMENT '服务名',
    module          VARCHAR(50)     NOT NULL                 COMMENT '模块名',
    action_pattern  VARCHAR(200)    NOT NULL                 COMMENT '动作匹配模式',
    is_enabled      TINYINT         NOT NULL DEFAULT 1       COMMENT '是否启用',
    log_request     TINYINT         NOT NULL DEFAULT 1       COMMENT '记录请求参数',
    log_response    TINYINT         NOT NULL DEFAULT 0       COMMENT '记录响应数据',
    log_change      TINYINT         NOT NULL DEFAULT 1       COMMENT '记录数据变更',
    description     VARCHAR(500)    DEFAULT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_service_name (service_name),
    KEY idx_is_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计配置表';

-- -----------------------------------------------------------
-- 初始数据: 默认审计配置
-- -----------------------------------------------------------
INSERT INTO audit_config (service_name, module, action_pattern, is_enabled, log_request, log_response, log_change, description) VALUES
('auth',         'user',       '*',              1, 1, 0, 1, '用户管理操作审计'),
('auth',         'login',      '*',              1, 1, 0, 0, '登录操作审计'),
('task',         'task',       '*',              1, 1, 0, 1, '任务操作审计'),
('workflow',     'transition', '*',              1, 1, 0, 1, '状态转移审计'),
('workflow',     'blockage',   '*',              1, 1, 0, 1, '阻塞操作审计'),
('workflow',     'exception',  '*',              1, 1, 0, 1, '异常处理审计'),
('notification', 'notify',     '*',              1, 1, 0, 0, '通知发送审计');

