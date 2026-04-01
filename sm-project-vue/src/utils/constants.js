/**
 * 状态名称映射
 */
export const STATE_MAP = {
  PLANNING: '需求管理',
  ASSIGNED: '任务准备',
  IN_DEVELOPMENT: '开发中',
  PENDING_REVIEW: '待评审',
  IN_TEST: '测试中',
  PENDING_RELEASE: '待发布',
  RELEASED: '已上线',
  COMPLETED: '已完成',
  BLOCKED: '已阻塞'
}

/**
 * 状态颜色
 */
export const STATE_COLORS = {
  PLANNING: '#3498db',
  ASSIGNED: '#2ecc71',
  IN_DEVELOPMENT: '#f39c12',
  PENDING_REVIEW: '#e74c3c',
  IN_TEST: '#9b59b6',
  PENDING_RELEASE: '#1abc9c',
  RELEASED: '#27ae60',
  COMPLETED: '#34495e',
  BLOCKED: '#95a5a6'
}

/**
 * 优先级
 */
export const PRIORITY_MAP = {
  LOW: { label: '低', type: 'info' },
  MEDIUM: { label: '中', type: 'warning' },
  HIGH: { label: '高', type: 'danger' },
  URGENT: { label: '紧急', type: 'danger' }
}

/**
 * 角色映射
 */
export const ROLE_MAP = {
  PM: { label: '项目经理', color: '#409EFF' },
  DEV: { label: '开发人员', color: '#67C23A' },
  TEST: { label: '测试人员', color: '#E6A23C' },
  ADMIN: { label: '系统管理员', color: '#F56C6C' }
}

