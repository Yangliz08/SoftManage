# SoftManage - 软件项目管理系统

基于 Spring Boot 3 + Vue 3 + Element Plus 的微服务软件项目管理系统。

## 📦 项目结构

```
SoftManage/
├── sm-project/                    后端微服务（Java 17 + Spring Boot 3）
│   ├── sm-common/                 公共模块（工具类、常量、DTO、异常处理）
│   ├── sm-gateway/     (8080)     API 网关（路由转发、JWT校验、限流）
│   ├── sm-auth/        (8001)     认证服务（登录、JWT、用户管理、权限）
│   ├── sm-task/        (8002)     任务服务（任务CRUD、分配、看板、Redis缓存）
│   ├── sm-workflow/    (8003)     工作流服务（状态机引擎、异常处理、阻塞恢复、闭环验证）
│   ├── sm-report/      (8004)     报表服务（仪表板、进度统计、甘特图、工作量分析）
│   ├── sm-notification/(8005)     通知服务（系统通知、邮件通知、MQ事件监听）
│   └── sm-audit/       (8006)     审计服务（操作日志、数据变更记录）
│
├── sm-project-vue/                前端（Vue 3 + Element Plus + Pinia + ECharts）
│   ├── src/pages/Login/           登录页面
│   ├── src/pages/PM/              PM系统（仪表板、看板、甘特图、需求、任务分配、流程配置）
│   ├── src/pages/DEV/             DEV系统（我的任务、代码提交、评审状态、工作量统计）
│   ├── src/pages/TEST/            TEST系统（测试任务、测试执行、缺陷报告、闭环验证）
│   ├── src/pages/ADMIN/           ADMIN系统（发布审批、用户管理、权限配置、系统日志）
│   ├── src/components/            公共组件（Layout、Header、Sidebar）
│   ├── src/api/                   API层（auth、task、workflow、report、notify、audit）
│   ├── src/stores/                Pinia状态管理（auth、cache）
│   └── src/utils/                 工具函数（token、constants、format）
│
└── sql/                           数据库初始化脚本
    ├── init-all.sql               总入口（创建6个数据库）
    ├── auth_db/init.sql           认证库（用户、角色、权限、初始数据）
    ├── task_db/init.sql           任务库（项目、需求、任务、缺陷）
    ├── workflow_db/init.sql       工作流库（流程定义、转移规则、状态历史、阻塞、异常、闭环链）
    ├── report_db/init.sql         报表库（进度快照、工作量、异常统计、甘特图）
    ├── notification_db/init.sql   通知库（通知记录、模板、设置）
    └── audit_db/init.sql          审计库（审计日志、数据变更、审计配置）
```

## 🏗 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Element Plus + Pinia + Vue Router + ECharts + Axios |
| 网关 | Spring Cloud Gateway + JWT Filter |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring AMQP |
| 缓存 | Redis（仅用于看板数据，5分钟TTL） |
| 消息 | RabbitMQ（服务间异步通知） |
| 数据库 | MySQL 8.0（6个独立数据库） |
| 认证 | JWT + BCrypt |

## 🚀 快速启动

### 1. 初始化数据库
```bash
mysql -u root -p < sql/init-all.sql
```

### 2. 启动中间件
```bash
redis-server
rabbitmq-server
```

### 3. 启动后端（按顺序）
```bash
cd sm-project/sm-gateway && mvn spring-boot:run
cd sm-project/sm-auth && mvn spring-boot:run
cd sm-project/sm-task && mvn spring-boot:run
cd sm-project/sm-workflow && mvn spring-boot:run
cd sm-project/sm-report && mvn spring-boot:run
cd sm-project/sm-notification && mvn spring-boot:run
cd sm-project/sm-audit && mvn spring-boot:run
```

### 4. 启动前端
```bash
cd sm-project-vue && npm install && npm run dev
```

### 5. 访问系统
- 前端: http://localhost:3000
- 网关: http://localhost:8080

### 演示账号
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 项目经理 | pm_user | admin123 |
| 开发人员 | dev_user | admin123 |
| 测试人员 | test_user | admin123 |

## 📊 核心功能

### 状态流转引擎（8个状态 + 4种异常流）
```
需求管理 → 任务准备 → 开发中 → 待评审 → 测试中 → 待发布 → 已上线 → 已完成
                                  ↗ 评审驳回 ↘
                                  ↗ 测试失败 ↘
                                  ↗ 发布拒绝 ↘
                           可在任意阶段标记阻塞 → 使用 previous_state 精确恢复
```

### Redis 缓存策略（仅看板）
- 看板数据缓存 5 分钟（TTL）
- 任务变更后自动失效缓存
- 前端 sessionStorage 二级缓存

### 闭环验证链
- 异常发生 → 创建修复任务 → 修复完成 → 评审通过 → 测试通过 → 闭环完成

## 📁 文件统计
- 后端 Java 文件: 95 个
- 前端 Vue/JS 文件: 37 个
- SQL 文件: 7 个
- 总文件数: 156 个
