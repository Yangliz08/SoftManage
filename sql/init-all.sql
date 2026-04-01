-- ============================================================
-- SoftManage 软件项目管理系统 - 数据库初始化总入口
-- ============================================================
-- 执行方式: mysql -u root -p < sql/init-all.sql
-- ============================================================

-- 创建所有数据库
CREATE DATABASE IF NOT EXISTS sm_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS sm_task DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS sm_workflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS sm_report DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS sm_notification DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS sm_audit DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 依次初始化各数据库
SOURCE sql/auth_db/init.sql;
SOURCE sql/task_db/init.sql;
SOURCE sql/workflow_db/init.sql;
SOURCE sql/report_db/init.sql;
SOURCE sql/notification_db/init.sql;
SOURCE sql/audit_db/init.sql;

