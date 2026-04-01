import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/token.js'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../components/Layout/Layout.vue'),
    redirect: '/pm/dashboard',
    children: [
      // === PM 页面 ===
      { path: '/pm/dashboard', name: 'PMDashboard', component: () => import('../pages/PM/Dashboard.vue'), meta: { title: '仪表板', role: 'PM' } },
      { path: '/pm/task-board', name: 'TaskBoard', component: () => import('../pages/PM/TaskBoard.vue'), meta: { title: '任务看板', role: 'PM' } },
      { path: '/pm/gantt', name: 'GanttChart', component: () => import('../pages/PM/GanttChart.vue'), meta: { title: '甘特图', role: 'PM' } },
      { path: '/pm/workflow-config', name: 'WorkflowConfig', component: () => import('../pages/PM/WorkflowConfig.vue'), meta: { title: '流程配置', role: 'PM' } },
      { path: '/pm/requirement', name: 'RequirementMgmt', component: () => import('../pages/PM/RequirementMgmt.vue'), meta: { title: '需求管理', role: 'PM' } },
      { path: '/pm/task-allocation', name: 'TaskAllocation', component: () => import('../pages/PM/TaskAllocation.vue'), meta: { title: '任务分配', role: 'PM' } },

      // === DEV 页面 ===
      { path: '/dev/my-tasks', name: 'MyTasks', component: () => import('../pages/DEV/MyTasks.vue'), meta: { title: '我的任务', role: 'DEV' } },
      { path: '/dev/code-submit', name: 'CodeSubmit', component: () => import('../pages/DEV/CodeSubmit.vue'), meta: { title: '代码提交', role: 'DEV' } },
      { path: '/dev/review-status', name: 'ReviewStatus', component: () => import('../pages/DEV/ReviewStatus.vue'), meta: { title: '评审状态', role: 'DEV' } },
      { path: '/dev/workload', name: 'WorkloadStats', component: () => import('../pages/DEV/WorkloadStats.vue'), meta: { title: '工作量统计', role: 'DEV' } },

      // === TEST 页面 ===
      { path: '/test/test-tasks', name: 'TestTasks', component: () => import('../pages/TEST/TestTasks.vue'), meta: { title: '测试任务', role: 'TEST' } },
      { path: '/test/test-execution', name: 'TestExecution', component: () => import('../pages/TEST/TestExecution.vue'), meta: { title: '测试执行', role: 'TEST' } },
      { path: '/test/defect-report', name: 'DefectReport', component: () => import('../pages/TEST/DefectReport.vue'), meta: { title: '缺陷报告', role: 'TEST' } },
      { path: '/test/closure-chain', name: 'ClosureChain', component: () => import('../pages/TEST/ClosureChain.vue'), meta: { title: '闭环验证', role: 'TEST' } },

      // === ADMIN 页面 ===
      { path: '/admin/release', name: 'ReleaseApproval', component: () => import('../pages/ADMIN/ReleaseApproval.vue'), meta: { title: '发布审批', role: 'ADMIN' } },
      { path: '/admin/user-mgmt', name: 'UserManagement', component: () => import('../pages/ADMIN/UserManagement.vue'), meta: { title: '用户管理', role: 'ADMIN' } },
      { path: '/admin/permission', name: 'PermissionConfig', component: () => import('../pages/ADMIN/PermissionConfig.vue'), meta: { title: '权限配置', role: 'ADMIN' } },
      { path: '/admin/system-log', name: 'SystemLog', component: () => import('../pages/ADMIN/SystemLog.vue'), meta: { title: '系统日志', role: 'ADMIN' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.meta.requiresAuth === false) {
    next()
  } else if (!token) {
    next('/login')
  } else {
    document.title = to.meta.title ? `${to.meta.title} - SoftManage` : 'SoftManage'
    next()
  }
})

export default router

