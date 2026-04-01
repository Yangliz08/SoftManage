('BUG-001', '登录接口在高并发下偶发500错误', '压测时TPS超过500后出现', 2, 1, 'HIGH', 'FIXED', 4, 3, '1.使用JMeter并发500请求\n2.观察响应', '全部返回200', '约5%请求返回500'),
('BUG-002', '购物车数量更新后总价计算错误', '修改数量后合计金额未刷新', 5, 1, 'MEDIUM', 'OPEN', 4, 3, '1.加入商品到购物车\n2.修改数量\n3.观察总价', '总价实时更新', '总价未更新'),
('BUG-003', '搜索结果分页跳转后页面空白', 'Safari浏览器特有问题', 4, 1, 'LOW', 'CONFIRMED', 4, 3, '1.使用Safari\n2.搜索商品\n3.翻页', '正常显示', '页面空白');
(1, 2, '数据库设计已完成评审，代码规范符合要求', 'REVIEW'),
(2, 2, '认证模块代码质量良好，注意token刷新逻辑', 'REVIEW'),
(7, 3, '支付模块已完成开发，提交PR: https://github.com/example/pr/42', 'COMMENT'),
(8, 3, '订单状态机正在开发中，预计明天完成核心逻辑', 'COMMENT'),
(14, 3, '等待推送服务商提供新的SDK，暂时无法继续开发', 'COMMENT');
UPDATE task SET block_reason = '第三方推送服务商接口变更，等待新文档' WHERE task_code = 'TASK-20260207-014';
('TASK-20260101-001', '数据库设计与建模', '设计核心数据库表结构，编写建库脚本', 1, 1, 'NORMAL', 'COMPLETED', 'RELEASED', 'HIGH', 100, 2, 3, 2, 4, 16.00, 15.00, '2026-01-05', '2026-01-10', '2026-01-05', '2026-01-09', 0),
('TASK-20260101-002', '用户认证模块开发', '实现JWT认证、登录注册接口', 1, 1, 'NORMAL', 'COMPLETED', 'RELEASED', 'HIGH', 100, 2, 3, 2, 4, 24.00, 22.00, '2026-01-08', '2026-01-15', '2026-01-08', '2026-01-14', 0),
('TASK-20260115-003', '商品列表API开发', '商品分页查询、详情查询接口', 1, 2, 'NORMAL', 'RELEASED', 'PENDING_RELEASE', 'HIGH', 100, 2, 3, 2, 4, 20.00, 18.00, '2026-01-15', '2026-01-22', '2026-01-15', '2026-01-22', 0),
('TASK-20260120-004', '搜索功能前端实现', '实现搜索框、搜索结果页、筛选条件', 1, 2, 'NORMAL', 'PENDING_RELEASE', 'IN_TEST', 'HIGH', 95, 2, 3, 2, 4, 30.00, NULL, '2026-01-20', '2026-02-05', '2026-01-20', NULL, 0),
('TASK-20260125-005', '购物车功能开发', '购物车增删改查、数量修改', 1, 3, 'NORMAL', 'IN_TEST', 'PENDING_REVIEW', 'MEDIUM', 80, 2, 3, 2, 4, 24.00, NULL, '2026-01-25', '2026-02-10', '2026-01-25', NULL, 0),
('TASK-20260126-006', '结算流程后端接口', '下单、库存检查、优惠券计算', 1, 3, 'NORMAL', 'IN_TEST', 'PENDING_REVIEW', 'HIGH', 85, 2, 3, 2, 4, 32.00, NULL, '2026-01-26', '2026-02-12', '2026-01-26', NULL, 0),
('TASK-20260201-007', '支付集成（支付宝/微信）', '集成第三方支付SDK，处理回调', 1, 3, 'NORMAL', 'PENDING_REVIEW', 'IN_DEVELOPMENT', 'HIGH', 90, 2, 3, 2, 4, 40.00, NULL, '2026-02-01', '2026-02-20', '2026-02-01', NULL, 0),
('TASK-20260205-008', '订单状态机实现', '订单创建/支付/发货/收货/退款流程', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'ASSIGNED', 'HIGH', 60, 2, 3, 2, 4, 48.00, NULL, '2026-02-05', '2026-02-28', '2026-02-05', NULL, 0),
('TASK-20260206-009', '物流查询接口对接', '对接顺丰、京东等物流API', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'ASSIGNED', 'MEDIUM', 40, 2, 3, 2, 4, 24.00, NULL, '2026-02-06', '2026-02-25', '2026-02-06', NULL, 0),
('TASK-20260210-010', '商品评价功能', '用户评价、图片上传、评分统计', 1, 4, 'NORMAL', 'ASSIGNED', 'PLANNING', 'MEDIUM', 0, 2, 3, 2, 4, 32.00, NULL, '2026-02-10', '2026-03-01', NULL, NULL, 0),
('TASK-20260211-011', '用户收藏夹功能', '收藏商品、收藏店铺功能', 1, 4, 'NORMAL', 'ASSIGNED', 'PLANNING', 'LOW', 0, 2, 3, NULL, 4, 16.00, NULL, '2026-02-15', '2026-03-05', NULL, NULL, 0),
('TASK-20260215-012', 'APP首页轮播图', 'Banner配置、广告投放', 2, 5, 'NORMAL', 'PLANNING', NULL, 'MEDIUM', 0, 2, NULL, NULL, NULL, 20.00, NULL, '2026-03-01', '2026-03-15', NULL, NULL, 0),
('TASK-20260216-013', '个性化推荐算法', '基于用户行为的商品推荐', 2, 5, 'NORMAL', 'PLANNING', NULL, 'HIGH', 0, 2, NULL, NULL, NULL, 80.00, NULL, '2026-03-10', '2026-04-30', NULL, NULL, 0),
('TASK-20260207-014', '消息推送系统', '站内信、APP推送、短信通知', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'IN_DEVELOPMENT', 'HIGH', 30, 2, 3, 2, 4, 40.00, NULL, '2026-02-07', '2026-02-28', '2026-02-07', NULL, 1);
('REQ-001', '用户登录与注册功能', '支持手机号、邮箱注册登录，第三方OAuth', 1, 'HIGH', 'IN_PROGRESS', 2, 3, 40),
('REQ-002', '商品搜索与筛选', '支持全文检索、分类筛选、价格排序', 1, 'HIGH', 'APPROVED', 2, 3, 60),
('REQ-003', '购物车与结算流程', '购物车管理、优惠券、多种支付方式', 1, 'MEDIUM', 'APPROVED', 2, 3, 80),
('REQ-004', '订单管理系统', '订单状态跟踪、退款、物流查询', 1, 'HIGH', 'DRAFT', 2, NULL, 100),
('REQ-005', 'APP首页设计', '首页Banner、推荐算法、个性化展示', 2, 'MEDIUM', 'DRAFT', 2, NULL, 50);
-- ============================================================
-- Task Service 任务数据库 (sm_task)
-- ============================================================
USE sm_task;

-- -----------------------------------------------------------
-- 1. 项目表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS project (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '项目ID',
    project_code    VARCHAR(50)     NOT NULL                 COMMENT '项目编码',
    project_name    VARCHAR(200)    NOT NULL                 COMMENT '项目名称',
    description     TEXT            DEFAULT NULL             COMMENT '项目描述',
    manager_id      BIGINT          NOT NULL                 COMMENT '项目经理ID',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, COMPLETED, ARCHIVED',
    start_date      DATE            DEFAULT NULL             COMMENT '计划开始日期',
    end_date        DATE            DEFAULT NULL             COMMENT '计划结束日期',
    actual_start    DATE            DEFAULT NULL             COMMENT '实际开始日期',
    actual_end      DATE            DEFAULT NULL             COMMENT '实际结束日期',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_code (project_code),
    KEY idx_manager_id (manager_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- -----------------------------------------------------------
-- 2. 需求表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS requirement (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '需求ID',
    req_code        VARCHAR(50)     NOT NULL                 COMMENT '需求编码',
    req_name        VARCHAR(200)    NOT NULL                 COMMENT '需求名称',
    description     TEXT            DEFAULT NULL             COMMENT '需求描述',
    project_id      BIGINT          NOT NULL                 COMMENT '所属项目ID',
    priority        VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级: LOW, MEDIUM, HIGH, URGENT',
    status          VARCHAR(20)     NOT NULL DEFAULT 'DRAFT'  COMMENT '状态: DRAFT, APPROVED, IN_PROGRESS, COMPLETED, CANCELLED',
    creator_id      BIGINT          NOT NULL                 COMMENT '创建人ID',
    assignee_id     BIGINT          DEFAULT NULL             COMMENT '负责人ID',
    estimated_hours DECIMAL(10,2)   DEFAULT NULL             COMMENT '预估工时(小时)',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_req_code (req_code),
    KEY idx_project_id (project_id),
    KEY idx_status (status),
    KEY idx_creator_id (creator_id),
    KEY idx_assignee_id (assignee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求表';

-- -----------------------------------------------------------
-- 3. 任务表 ⭐ 核心表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '任务ID',
    task_code       VARCHAR(50)     NOT NULL                 COMMENT '任务编码',
    task_name       VARCHAR(200)    NOT NULL                 COMMENT '任务名称',
    description     TEXT            DEFAULT NULL             COMMENT '任务描述',
    project_id      BIGINT          NOT NULL                 COMMENT '所属项目ID',
    requirement_id  BIGINT          DEFAULT NULL             COMMENT '关联需求ID',
    parent_task_id  BIGINT          DEFAULT NULL             COMMENT '父任务ID（用于修复任务）',
    task_type       VARCHAR(30)     NOT NULL DEFAULT 'NORMAL' COMMENT '任务类型: NORMAL, FIX, HOTFIX',
    current_state   VARCHAR(30)     NOT NULL DEFAULT 'PLANNING' COMMENT '当前状态',
    previous_state  VARCHAR(30)     DEFAULT NULL             COMMENT '前置状态（用于阻塞恢复）',
    priority        VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级: LOW, MEDIUM, HIGH, URGENT',
    progress        INT             NOT NULL DEFAULT 0       COMMENT '进度百分比(0-100)',
    creator_id      BIGINT          NOT NULL                 COMMENT '创建人ID',
    assignee_id     BIGINT          DEFAULT NULL             COMMENT '当前负责人ID',
    reviewer_id     BIGINT          DEFAULT NULL             COMMENT '评审人ID',
    tester_id       BIGINT          DEFAULT NULL             COMMENT '测试人ID',
    estimated_hours DECIMAL(10,2)   DEFAULT NULL             COMMENT '预估工时',
    actual_hours    DECIMAL(10,2)   DEFAULT NULL             COMMENT '实际工时',
    start_date      DATE            DEFAULT NULL             COMMENT '计划开始日期',
    end_date        DATE            DEFAULT NULL             COMMENT '计划结束日期',
    actual_start    DATE            DEFAULT NULL             COMMENT '实际开始日期',
    actual_end      DATE            DEFAULT NULL             COMMENT '实际结束日期',
    code_url        VARCHAR(500)    DEFAULT NULL             COMMENT '代码仓库/PR链接',
    is_blocked      TINYINT         NOT NULL DEFAULT 0       COMMENT '是否被阻塞: 0=否, 1=是',
    block_reason    VARCHAR(500)    DEFAULT NULL             COMMENT '阻塞原因',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_code (task_code),
    KEY idx_project_id (project_id),
    KEY idx_requirement_id (requirement_id),
    KEY idx_current_state (current_state),
    KEY idx_assignee_id (assignee_id),
    KEY idx_creator_id (creator_id),
    KEY idx_priority (priority),
    KEY idx_is_blocked (is_blocked)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- -----------------------------------------------------------
-- 4. 任务评论表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_comment (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    task_id         BIGINT          NOT NULL                 COMMENT '任务ID',
    user_id         BIGINT          NOT NULL                 COMMENT '评论用户ID',
    content         TEXT            NOT NULL                 COMMENT '评论内容',
    comment_type    VARCHAR(20)     NOT NULL DEFAULT 'COMMENT' COMMENT '类型: COMMENT, REVIEW, FEEDBACK',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务评论表';

-- -----------------------------------------------------------
-- 5. 任务附件表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_attachment (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    task_id         BIGINT          NOT NULL                 COMMENT '任务ID',
    file_name       VARCHAR(200)    NOT NULL                 COMMENT '文件名',
    file_path       VARCHAR(500)    NOT NULL                 COMMENT '文件路径',
    file_size       BIGINT          DEFAULT NULL             COMMENT '文件大小(字节)',
    file_type       VARCHAR(50)     DEFAULT NULL             COMMENT '文件类型',
    uploader_id     BIGINT          NOT NULL                 COMMENT '上传人ID',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务附件表';

-- -----------------------------------------------------------
-- 6. 缺陷表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS defect (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '缺陷ID',
    defect_code     VARCHAR(50)     NOT NULL                 COMMENT '缺陷编码',
    defect_name     VARCHAR(200)    NOT NULL                 COMMENT '缺陷标题',
    description     TEXT            DEFAULT NULL             COMMENT '缺陷描述',
    task_id         BIGINT          NOT NULL                 COMMENT '关联任务ID',
    project_id      BIGINT          NOT NULL                 COMMENT '所属项目ID',
    severity        VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度: LOW, MEDIUM, HIGH, CRITICAL',
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN'   COMMENT '状态: OPEN, CONFIRMED, FIXING, FIXED, VERIFIED, CLOSED, REJECTED',
    reporter_id     BIGINT          NOT NULL                 COMMENT '报告人ID',
    assignee_id     BIGINT          DEFAULT NULL             COMMENT '负责人ID',
    steps_to_reproduce TEXT         DEFAULT NULL             COMMENT '复现步骤',
    expected_result TEXT            DEFAULT NULL             COMMENT '期望结果',
    actual_result   TEXT            DEFAULT NULL             COMMENT '实际结果',
    environment     VARCHAR(200)    DEFAULT NULL             COMMENT '测试环境',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_defect_code (defect_code),
    KEY idx_task_id (task_id),
    KEY idx_project_id (project_id),
    KEY idx_status (status),
    KEY idx_reporter_id (reporter_id),
    KEY idx_assignee_id (assignee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缺陷表';

('PROJ-2026-001', 'SoftManage项目管理系统V1.0', '基于可定制工作流的软件项目管理系统，支持异常闭环与previous_state回溯恢复机制', 2, 'ACTIVE', '2026-01-01', '2026-06-30'),
('PROJ-2026-002', 'SoftManage系统优化与扩展', '系统性能优化、流程模板库扩展、多项目并行管理能力增强', 2, 'ACTIVE', '2026-03-01', '2026-08-31');
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_tag (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    tag_name        VARCHAR(50)     NOT NULL                 COMMENT '标签名',
    color           VARCHAR(20)     DEFAULT '#409EFF'        COMMENT '标签颜色',
('REQ-001', '用户认证与权限管理', '实现JWT认证、四角色(PM/DEV/TEST/ADMIN)权限控制、前置可见性约束', 1, 'HIGH', 'IN_PROGRESS', 2, 3, 40),
('REQ-002', '可定制工作流引擎', '实现流程模板定义、状态转移规则配置、复合状态建模、异常闭环与previous_state回溯恢复', 1, 'HIGH', 'APPROVED', 2, 3, 80),
('REQ-003', '任务管理与看板系统', '任务CRUD、多状态看板视图、Redis缓存策略、进度自动采集与追踪', 1, 'HIGH', 'APPROVED', 2, 3, 60),
('REQ-004', '报表与数据可视化', '仪表板统计、甘特图、工作量分析、异常统计、报表导出', 1, 'MEDIUM', 'DRAFT', 2, NULL, 80),
('REQ-005', '通知与审计日志系统', '系统通知、邮件推送、操作审计日志、数据变更追踪', 2, 'MEDIUM', 'DRAFT', 2, NULL, 50);

-- -----------------------------------------------------------
-- 8. 任务-标签关联表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS task_tag_relation (
('TASK-20260101-001', '数据库架构设计', '设计认证库、任务库、工作流库、报表库、通知库、审计库表结构，编写初始化脚本', 1, 1, 'NORMAL', 'COMPLETED', 'RELEASED', 'HIGH', 100, 2, 3, 2, 4, 16.00, 15.00, '2026-01-05', '2026-01-10', '2026-01-05', '2026-01-09', 0),
('TASK-20260101-002', 'JWT认证与登录模块开发', '实现JWT令牌生成/验证、用户登录/登出、Token刷新、角色识别', 1, 1, 'NORMAL', 'COMPLETED', 'RELEASED', 'HIGH', 100, 2, 3, 2, 4, 24.00, 22.00, '2026-01-08', '2026-01-15', '2026-01-08', '2026-01-14', 0),
('TASK-20260115-003', '工作流引擎核心开发', '实现流程定义解析、状态机驱动、转移规则校验、复合状态建模', 1, 2, 'NORMAL', 'RELEASED', 'PENDING_RELEASE', 'HIGH', 100, 2, 3, 2, 4, 40.00, 38.00, '2026-01-15', '2026-01-30', '2026-01-15', '2026-01-29', 0),
('TASK-20260120-004', '状态转移规则管理', '实现可配置的状态转移规则CRUD、角色约束、前置条件校验', 1, 2, 'NORMAL', 'PENDING_RELEASE', 'IN_TEST', 'HIGH', 95, 2, 3, 2, 4, 24.00, NULL, '2026-01-25', '2026-02-10', '2026-01-25', NULL, 0),
('TASK-20260125-005', '任务看板前后端开发', '实现多状态列看板视图、任务卡片拖拽、状态筛选与角色过滤', 1, 3, 'NORMAL', 'IN_TEST', 'PENDING_REVIEW', 'HIGH', 80, 2, 3, 2, 4, 32.00, NULL, '2026-01-28', '2026-02-15', '2026-01-28', NULL, 0),
('TASK-20260126-006', 'Redis看板缓存层实现', '实现看板数据Redis缓存、5分钟TTL策略、状态变更自动失效', 1, 3, 'NORMAL', 'IN_TEST', 'PENDING_REVIEW', 'MEDIUM', 85, 2, 3, 2, 4, 16.00, NULL, '2026-02-01', '2026-02-10', '2026-02-01', NULL, 0),
('TASK-20260201-007', '异常闭环处理机制', '实现评审驳回→修复→复审、测试失败→修复→复测、发布拒绝→修复闭环流程', 1, 2, 'NORMAL', 'PENDING_REVIEW', 'IN_DEVELOPMENT', 'HIGH', 90, 2, 3, 2, 4, 32.00, NULL, '2026-02-05', '2026-02-20', '2026-02-05', NULL, 0),
('TASK-20260205-008', 'previous_state回溯恢复机制', '实现阻塞状态记录previous_state、恢复时精确还原阶段、审计日志关联', 1, 2, 'NORMAL', 'IN_DEVELOPMENT', 'ASSIGNED', 'HIGH', 60, 2, 3, 2, 4, 24.00, NULL, '2026-02-10', '2026-02-25', '2026-02-10', NULL, 0),
('TASK-20260206-009', '甘特图数据接口开发', '实现任务依赖关系计算、里程碑标记、计划与实际进度对比数据', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'ASSIGNED', 'MEDIUM', 40, 2, 3, 2, 4, 24.00, NULL, '2026-02-12', '2026-02-28', '2026-02-12', NULL, 0),
('TASK-20260210-010', '报表统计与导出功能', '实现进度快照定时生成、工作量统计、异常统计、Excel/PDF导出', 1, 4, 'NORMAL', 'ASSIGNED', 'PLANNING', 'MEDIUM', 0, 2, 3, 2, 4, 32.00, NULL, '2026-02-20', '2026-03-10', NULL, NULL, 0),
('TASK-20260211-011', '通知服务与消息模板', '实现系统内通知、邮件通知、通知模板变量替换、用户偏好设置', 1, 5, 'NORMAL', 'ASSIGNED', 'PLANNING', 'MEDIUM', 0, 2, 3, NULL, 4, 20.00, NULL, '2026-02-25', '2026-03-10', NULL, NULL, 0),
('TASK-20260215-012', '需求管理模块开发', '实现需求CRUD、需求与任务关联、需求状态流转、优先级管理', 2, 3, 'NORMAL', 'PLANNING', NULL, 'MEDIUM', 0, 2, NULL, NULL, NULL, 24.00, NULL, '2026-03-15', '2026-04-01', NULL, NULL, 0),
('TASK-20260216-013', '角色权限配置管理', '实现角色CRUD、权限树管理、按流程节点粒度的可见性控制', 2, 1, 'NORMAL', 'PLANNING', NULL, 'HIGH', 0, 2, NULL, NULL, NULL, 32.00, NULL, '2026-03-20', '2026-04-15', NULL, NULL, 0),
('TASK-20260207-014', '审计日志服务开发', '实现操作审计记录、数据变更追踪、审计配置管理、日志查询接口', 1, 5, 'NORMAL', 'IN_DEVELOPMENT', 'IN_DEVELOPMENT', 'HIGH', 30, 2, 3, 2, 4, 28.00, NULL, '2026-02-15', '2026-03-05', '2026-02-15', NULL, 1);

-- -----------------------------------------------------------
UPDATE task SET block_reason = 'RabbitMQ消息队列集群部署未完成，等待运维环境就绪' WHERE task_code = 'TASK-20260207-014';
-- -----------------------------------------------------------
INSERT INTO requirement (req_code, req_name, description, project_id, priority, status, creator_id, assignee_id, estimated_hours) VALUES
('REQ-001', '用户登录与注册功能', '支持手机号、邮箱注册登录，第三方OAuth', 1, 'HIGH', 'IN_PROGRESS', 2, 3, 40),
('REQ-002', '商品搜索与筛选', '支持全文检索、分类筛选、价格排序', 1, 'HIGH', 'APPROVED', 2, 3, 60),
('REQ-003', '购物车与结算流程', '购物车管理、优惠券、多种支付方式', 1, 'MEDIUM', 'APPROVED', 2, 3, 80),
(1, 2, '六个微服务数据库表结构设计已通过评审，索引策略合理', 'REVIEW'),
(2, 2, 'JWT认证模块代码质量良好，注意Token刷新时的并发安全', 'REVIEW'),
(7, 3, '异常闭环处理机制已完成开发，提交PR待评审，覆盖三种异常回退场景', 'COMMENT'),
(8, 3, 'previous_state回溯恢复机制核心逻辑已完成，正在补充阻塞恢复的集成测试', 'COMMENT'),
(14, 3, '等待RabbitMQ集群环境部署完成，审计事件监听功能暂时无法联调测试', 'COMMENT');
-- -----------------------------------------------------------
INSERT INTO task (task_code, task_name, description, project_id, requirement_id, task_type, current_state, previous_state, priority, progress, creator_id, assignee_id, reviewer_id, tester_id, estimated_hours, actual_hours, start_date, end_date, actual_start, actual_end, is_blocked) VALUES
('TASK-20260101-001', '数据库设计与建模', '设计核心数据库表结构，编写建库脚本', 1, 1, 'NORMAL', 'COMPLETED', 'RELEASED', 'HIGH', 100, 2, 3, 2, 4, 16.00, 15.00, '2026-01-05', '2026-01-10', '2026-01-05', '2026-01-09', 0),
('TASK-20260101-002', '用户认证模块开发', '实现JWT认证、登录注册接口', 1, 1, 'NORMAL', 'COMPLETED', 'RELEASED', 'HIGH', 100, 2, 3, 2, 4, 24.00, 22.00, '2026-01-08', '2026-01-15', '2026-01-08', '2026-01-14', 0),
('TASK-20260115-003', '商品列表API开发', '商品分页查询、详情查询接口', 1, 2, 'NORMAL', 'RELEASED', 'PENDING_RELEASE', 'HIGH', 100, 2, 3, 2, 4, 20.00, 18.00, '2026-01-15', '2026-01-22', '2026-01-15', '2026-01-22', 0),
('BUG-001', '并发状态转移时偶发数据不一致', '两个用户同时对同一任务执行状态转移操作时出现', 3, 1, 'HIGH', 'FIXED', 4, 3, '1.用户A执行评审通过\n2.用户B同时执行评审驳回\n3.观察最终状态', '只有一个操作成功，另一个提示冲突', '两个操作均执行成功，状态混乱'),
('BUG-002', '阻塞恢复后看板缓存未及时失效', '解除阻塞后看板仍显示阻塞状态直到缓存过期', 6, 1, 'MEDIUM', 'OPEN', 4, 3, '1.将任务标记为阻塞\n2.解除阻塞\n3.刷新看板页面', '看板立即显示恢复后的状态', '看板仍显示阻塞状态，需等5分钟缓存过期'),
('BUG-003', '闭环验证链在Safari浏览器下步骤显示异常', 'Safari浏览器特有的CSS渲染问题', 7, 1, 'LOW', 'CONFIRMED', 4, 3, '1.使用Safari浏览器\n2.打开闭环验证页面\n3.查看验证链步骤', '四个步骤图标正常显示', '步骤连线断裂，图标错位');
('TASK-20260201-007', '支付集成（支付宝/微信）', '集成第三方支付SDK，处理回调', 1, 3, 'NORMAL', 'PENDING_REVIEW', 'IN_DEVELOPMENT', 'HIGH', 90, 2, 3, 2, 4, 40.00, NULL, '2026-02-01', '2026-02-20', '2026-02-01', NULL, 0),
('TASK-20260205-008', '订单状态机实现', '订单创建/支付/发货/收货/退款流程', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'ASSIGNED', 'HIGH', 60, 2, 3, 2, 4, 48.00, NULL, '2026-02-05', '2026-02-28', '2026-02-05', NULL, 0),
('TASK-20260206-009', '物流查询接口对接', '对接顺丰、京东等物流API', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'ASSIGNED', 'MEDIUM', 40, 2, 3, 2, 4, 24.00, NULL, '2026-02-06', '2026-02-25', '2026-02-06', NULL, 0),
('TASK-20260210-010', '商品评价功能', '用户评价、图片上传、评分统计', 1, 4, 'NORMAL', 'ASSIGNED', 'PLANNING', 'MEDIUM', 0, 2, 3, 2, 4, 32.00, NULL, '2026-02-10', '2026-03-01', NULL, NULL, 0),
('TASK-20260211-011', '用户收藏夹功能', '收藏商品、收藏店铺功能', 1, 4, 'NORMAL', 'ASSIGNED', 'PLANNING', 'LOW', 0, 2, 3, NULL, 4, 16.00, NULL, '2026-02-15', '2026-03-05', NULL, NULL, 0),
('TASK-20260215-012', 'APP首页轮播图', 'Banner配置、广告投放', 2, 5, 'NORMAL', 'PLANNING', NULL, 'MEDIUM', 0, 2, NULL, NULL, NULL, 20.00, NULL, '2026-03-01', '2026-03-15', NULL, NULL, 0),
('TASK-20260216-013', '个性化推荐算法', '基于用户行为的商品推荐', 2, 5, 'NORMAL', 'PLANNING', NULL, 'HIGH', 0, 2, NULL, NULL, NULL, 80.00, NULL, '2026-03-10', '2026-04-30', NULL, NULL, 0),
('TASK-20260207-014', '消息推送系统', '站内信、APP推送、短信通知', 1, 4, 'NORMAL', 'IN_DEVELOPMENT', 'IN_DEVELOPMENT', 'HIGH', 30, 2, 3, 2, 4, 40.00, NULL, '2026-02-07', '2026-02-28', '2026-02-07', NULL, 1);

-- 更新被阻塞任务的阻塞原因
UPDATE task SET block_reason = '第三方推送服务商接口变更，等待新文档' WHERE task_code = 'TASK-20260207-014';

-- -----------------------------------------------------------
-- 初始数据: 任务评论示例
-- -----------------------------------------------------------
INSERT INTO task_comment (task_id, user_id, content, comment_type) VALUES
(1, 2, '数据库设计已完成评审，代码规范符合要求', 'REVIEW'),
(2, 2, '认证模块代码质量良好，注意token刷新逻辑', 'REVIEW'),
(7, 3, '支付模块已完成开发，提交PR: https://github.com/example/pr/42', 'COMMENT'),
(8, 3, '订单状态机正在开发中，预计明天完成核心逻辑', 'COMMENT'),
(14, 3, '等待推送服务商提供新的SDK，暂时无法继续开发', 'COMMENT');

-- -----------------------------------------------------------
-- 初始数据: 缺陷示例
-- -----------------------------------------------------------
INSERT INTO defect (defect_code, defect_name, description, task_id, project_id, severity, status, reporter_id, assignee_id, steps_to_reproduce, expected_result, actual_result) VALUES
('BUG-001', '登录接口在高并发下偶发500错误', '压测时TPS超过500后出现', 2, 1, 'HIGH', 'FIXED', 4, 3, '1.使用JMeter并发500请求\n2.观察响应', '全部返回200', '约5%请求返回500'),
('BUG-002', '购物车数量更新后总价计算错误', '修改数量后合计金额未刷新', 5, 1, 'MEDIUM', 'OPEN', 4, 3, '1.加入商品到购物车\n2.修改数量\n3.观察总价', '总价实时更新', '总价未更新'),
('BUG-003', '搜索结果分页跳转后页面空白', 'Safari浏览器特有问题', 4, 1, 'LOW', 'CONFIRMED', 4, 3, '1.使用Safari\n2.搜索商品\n3.翻页', '正常显示', '页面空白');
