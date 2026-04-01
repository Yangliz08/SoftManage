-- ============================================================
-- Auth Service 认证数据库 (sm_auth)
-- ============================================================
USE sm_auth;

-- -----------------------------------------------------------
-- 1. 用户表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    password        VARCHAR(200)    NOT NULL                 COMMENT '密码(BCrypt加密)',
    real_name       VARCHAR(50)     DEFAULT NULL             COMMENT '真实姓名',
    email           VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    phone           VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    avatar          VARCHAR(500)    DEFAULT NULL             COMMENT '头像URL',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0=禁用, 1=正常',
    last_login_time DATETIME        DEFAULT NULL             COMMENT '最后登录时间',
    last_login_ip   VARCHAR(50)     DEFAULT NULL             COMMENT '最后登录IP',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_email (email),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 角色表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '角色ID',
    role_code       VARCHAR(50)     NOT NULL                 COMMENT '角色编码: PM, DEV, TEST, ADMIN',
    role_name       VARCHAR(100)    NOT NULL                 COMMENT '角色名称',
    description     VARCHAR(500)    DEFAULT NULL             COMMENT '角色描述',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0=禁用, 1=正常',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- -----------------------------------------------------------
-- 3. 权限表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '权限ID',
    parent_id       BIGINT          DEFAULT 0                COMMENT '父权限ID',
    perm_code       VARCHAR(100)    NOT NULL                 COMMENT '权限编码',
    perm_name       VARCHAR(100)    NOT NULL                 COMMENT '权限名称',
    perm_type       TINYINT         NOT NULL DEFAULT 1       COMMENT '类型: 1=菜单, 2=按钮, 3=接口',
    path            VARCHAR(200)    DEFAULT NULL             COMMENT '路由路径',
    icon            VARCHAR(100)    DEFAULT NULL             COMMENT '菜单图标',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- -----------------------------------------------------------
-- 4. 用户-角色关联表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    role_id         BIGINT          NOT NULL                 COMMENT '角色ID',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- -----------------------------------------------------------
-- 5. 角色-权限关联表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    role_id         BIGINT          NOT NULL                 COMMENT '角色ID',
    permission_id   BIGINT          NOT NULL                 COMMENT '权限ID',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- -----------------------------------------------------------
-- 6. 登录日志表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_login_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          DEFAULT NULL             COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    login_type      TINYINT         NOT NULL DEFAULT 1       COMMENT '类型: 1=登录, 2=登出',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '结果: 0=失败, 1=成功',
    ip_address      VARCHAR(50)     DEFAULT NULL             COMMENT 'IP地址',
    user_agent      VARCHAR(500)    DEFAULT NULL             COMMENT '浏览器信息',
    message         VARCHAR(500)    DEFAULT NULL             COMMENT '备注信息',
    login_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- -----------------------------------------------------------
-- 初始数据: 角色
-- -----------------------------------------------------------
INSERT INTO sys_role (role_code, role_name, description, sort_order) VALUES
('PM',    '项目经理',   '负责需求管理、任务分配、看板管理、流程配置', 1),
('DEV',   '开发人员',   '负责任务开发、代码提交、评审状态查看',       2),
('TEST',  '测试人员',   '负责测试执行、缺陷报告、闭环验证',           3),
('ADMIN', '系统管理员', '负责发布审批、用户管理、权限配置、系统日志', 4);

-- -----------------------------------------------------------
-- 初始数据: 管理员账号 (密码: admin123, BCrypt加密)
-- -----------------------------------------------------------
INSERT INTO sys_user (username, password, real_name, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@softmanage.com', 1),
('pm_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '项目经理张三', 'pm@softmanage.com', 1),
('dev_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '开发李四', 'dev@softmanage.com', 1),
('test_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试王五', 'test@softmanage.com', 1);

-- 分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 4),  -- admin -> ADMIN
(2, 1),  -- pm_user -> PM
(3, 2),  -- dev_user -> DEV
(4, 3);  -- test_user -> TEST

-- -----------------------------------------------------------
-- 初始数据: 权限菜单
-- -----------------------------------------------------------
INSERT INTO sys_permission (parent_id, perm_code, perm_name, perm_type, path, icon, sort_order) VALUES
-- PM 菜单
(0, 'pm',                  'PM系统',      1, '/pm',                  'Monitor',     1),
(1, 'pm:dashboard',        '仪表板',      1, '/pm/dashboard',        'DataBoard',   1),
(1, 'pm:task-board',       '任务看板',    1, '/pm/task-board',       'Grid',        2),
(1, 'pm:gantt',            '甘特图',      1, '/pm/gantt',            'Calendar',    3),
(1, 'pm:workflow-config',  '流程配置',    1, '/pm/workflow-config',  'Setting',     4),
(1, 'pm:requirement',      '需求管理',    1, '/pm/requirement',      'Document',    5),
(1, 'pm:task-allocation',  '任务分配',    1, '/pm/task-allocation',  'UserFilled',  6),
-- DEV 菜单
(0, 'dev',                 'DEV系统',     1, '/dev',                 'Monitor',     2),
(9, 'dev:my-tasks',        '我的任务',    1, '/dev/my-tasks',        'List',        1),
(9, 'dev:code-submit',     '代码提交',    1, '/dev/code-submit',     'Upload',      2),
(9, 'dev:review-status',   '评审状态',    1, '/dev/review-status',   'View',        3),
(9, 'dev:workload',        '工作量统计',  1, '/dev/workload',        'TrendCharts', 4),
-- TEST 菜单
(0, 'test',                'TEST系统',    1, '/test',                'Monitor',     3),
(14,'test:test-tasks',     '测试任务',    1, '/test/test-tasks',     'List',        1),
(14,'test:test-execution', '测试执行',    1, '/test/test-execution', 'VideoPlay',   2),
(14,'test:defect-report',  '缺陷报告',   1, '/test/defect-report',  'Warning',     3),
(14,'test:closure-chain',  '闭环验证',   1, '/test/closure-chain',  'CircleCheck', 4),
-- ADMIN 菜单
(0, 'admin',               'ADMIN系统',   1, '/admin',               'Setting',     4),
(19,'admin:release',       '发布审批',    1, '/admin/release',       'Promotion',   1),
(19,'admin:user-mgmt',     '用户管理',    1, '/admin/user-mgmt',     'User',        2),
(19,'admin:permission',    '权限配置',    1, '/admin/permission',    'Lock',        3),
(19,'admin:system-log',    '系统日志',    1, '/admin/system-log',    'Notebook',    4);

-- 分配权限给角色
-- PM 拥有 PM 菜单权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1),(1, 2),(1, 3),(1, 4),(1, 5),(1, 6),(1, 7),(1, 8);
-- DEV 拥有 DEV 菜单权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 9),(2, 10),(2, 11),(2, 12),(2, 13);
-- TEST 拥有 TEST 菜单权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 14),(3, 15),(3, 16),(3, 17),(3, 18);
-- ADMIN 拥有全部权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(4, 1),(4, 2),(4, 3),(4, 4),(4, 5),(4, 6),(4, 7),(4, 8),
(4, 9),(4, 10),(4, 11),(4, 12),(4, 13),
(4, 14),(4, 15),(4, 16),(4, 17),(4, 18),
(4, 19),(4, 20),(4, 21),(4, 22),(4, 23);

