# SoftManage - 现代微服务项目管理系统

基于 Spring Boot 3 + Vue 3 + Element Plus 的企业级软件项目管理系统。

## 核心功能

### 状态流转引擎（8个状态 + 4种异常流）
```
需求管理 → 任务准备 → 开发中 → 待评审 → 测试中 → 待发布 → 已上线 → 已完成
                                  ↗ 评审驳回 ↘
                                  ↗ 测试失败 ↘
                                  ↗ 发布拒绝 ↘
                           可在任意阶段标记阻塞 -> 使用 previous_state 精确恢复
```

### Redis 缓存策略（仅看板）
- 看板数据缓存 5 分钟（TTL）
- 任务变更后自动失效缓存
- 前端 sessionStorage 二级缓存

### 闭环验证链
- 异常发生 -> 创建修复任务 -> 修复完成 -> 评审通过 -> 测试通过 -> 闭环完成

## 角色体系（4种角色）

| 角色 | 标识 | 核心职责 |
|------|------|----------|
| 系统管理员 | ADMIN | 用户管理、文件管理、全局数据访问 |
| 项目经理 | PM | 项目创建、需求管理、任务分配、进度监控、流程配置、审批 |
| 开发人员 | DEV | 接收任务、编码开发、提交代码、上传文件 |
| 测试人员 | TEST | 测试执行、缺陷报告、闭环验证、上传测试文件 |

## 项目结构

```text
SoftManage/
├── sm-project/                    后端微服务（Java 17 + Spring Boot 3）
│   ├── sm-common/                 公共模块（工具类、常量、DTO、异常处理）
│   ├── sm-gateway/     (8200)     API 网关（路由转发、JWT校验、限流）
│   ├── sm-auth/        (8201)     认证服务（登录、JWT、用户管理、权限）
│   ├── sm-task/        (8202)     任务服务（任务CRUD、分配、看板、Redis缓存）
│   ├── sm-workflow/    (8203)     工作流服务（状态机引擎、异常处理、阻塞恢复、闭环验证）
│   ├── sm-report/      (8204)     报表服务（仪表板、进度统计、甘特图、工作量分析）
│   ├── sm-notification/(8205)     通知服务（系统通知、邮件通知、MQ事件监听）
│   ├── sm-audit/       (8206)     审计服务（操作日志、数据变更记录）
│   └── sm-ai/          (8207)     AI服务（智能决策、数据分析）
│
├── sm-project-vue/      (3002)    前端（Vue 3 + Element Plus + Pinia + ECharts）
└── sql/                           数据库初始化脚本
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Element Plus + Pinia + Vue Router + ECharts + Axios |
| 网关 | Spring Cloud Gateway + JWT Filter |
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Spring AMQP |
| 缓存 | Redis（仅用于看板数据，5分钟TTL） |
| 消息 | RabbitMQ（服务间异步通知） |
| 数据库 | MySQL 8.0（6个独立数据库） |
| 认证 | JWT + BCrypt |

## 快速启动

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
cd sm-project/sm-ai && mvn spring-boot:run
```

### 4. 启动前端
```bash
cd sm-project-vue && npm install && npm run dev
```

### 5. 访问系统
- 前端: http://localhost:3002
- 网关: http://localhost:8200

## 演示账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 系统管理员 | sysadmin | sysadmin |
| 项目经理 | pm_user | 123 |
| 开发人员 | dev_user | 123 |
| 测试人员 | test_user | 123 |

详细账号请查看 `DEMO_ACCOUNTS_PASSWORD_LIST.md`。
