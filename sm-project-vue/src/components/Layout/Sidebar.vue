<template>
  <div class="sidebar-container">
    <div class="logo">
      <el-icon :size="24" color="#409EFF"><Monitor /></el-icon>
      <span v-show="!isCollapse" class="logo-text">SoftManage</span>
    </div>
    <el-menu
      :default-active="currentPath"
      :collapse="isCollapse"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
      :collapse-transition="false"
    >
      <!-- PM 菜单 -->
      <el-sub-menu v-if="showMenu('PM')" index="pm">
        <template #title><el-icon><DataBoard /></el-icon><span>PM系统</span></template>
        <el-menu-item index="/pm/dashboard"><el-icon><Odometer /></el-icon>仪表板</el-menu-item>
        <el-menu-item index="/pm/task-board"><el-icon><Grid /></el-icon>任务看板</el-menu-item>
        <el-menu-item index="/pm/gantt"><el-icon><Calendar /></el-icon>甘特图</el-menu-item>
        <el-menu-item index="/pm/requirement"><el-icon><Document /></el-icon>需求管理</el-menu-item>
        <el-menu-item index="/pm/task-allocation"><el-icon><UserFilled /></el-icon>任务分配</el-menu-item>
        <el-menu-item index="/pm/workflow-config"><el-icon><Setting /></el-icon>流程配置</el-menu-item>
      </el-sub-menu>

      <!-- DEV 菜单 -->
      <el-sub-menu v-if="showMenu('DEV')" index="dev">
        <template #title><el-icon><Monitor /></el-icon><span>DEV系统</span></template>
        <el-menu-item index="/dev/my-tasks"><el-icon><List /></el-icon>我的任务</el-menu-item>
        <el-menu-item index="/dev/code-submit"><el-icon><Upload /></el-icon>代码提交</el-menu-item>
        <el-menu-item index="/dev/review-status"><el-icon><View /></el-icon>评审状态</el-menu-item>
        <el-menu-item index="/dev/workload"><el-icon><TrendCharts /></el-icon>工作量统计</el-menu-item>
      </el-sub-menu>

      <!-- TEST 菜单 -->
      <el-sub-menu v-if="showMenu('TEST')" index="test">
        <template #title><el-icon><Finished /></el-icon><span>TEST系统</span></template>
        <el-menu-item index="/test/test-tasks"><el-icon><List /></el-icon>测试任务</el-menu-item>
        <el-menu-item index="/test/test-execution"><el-icon><VideoPlay /></el-icon>测试执行</el-menu-item>
        <el-menu-item index="/test/defect-report"><el-icon><Warning /></el-icon>缺陷报告</el-menu-item>
        <el-menu-item index="/test/closure-chain"><el-icon><CircleCheck /></el-icon>闭环验证</el-menu-item>
      </el-sub-menu>

      <!-- ADMIN 菜单 -->
      <el-sub-menu v-if="showMenu('ADMIN')" index="admin">
        <template #title><el-icon><Setting /></el-icon><span>ADMIN系统</span></template>
        <el-menu-item index="/admin/release"><el-icon><Promotion /></el-icon>发布审批</el-menu-item>
        <el-menu-item index="/admin/user-mgmt"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="/admin/permission"><el-icon><Lock /></el-icon>权限配置</el-menu-item>
        <el-menu-item index="/admin/system-log"><el-icon><Notebook /></el-icon>系统日志</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth.js'

defineProps({ isCollapse: Boolean })

const route = useRoute()
const authStore = useAuthStore()

const currentPath = computed(() => route.path)

function showMenu(role) {
  return authStore.isAdmin || authStore.roles.includes(role)
}
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
}
.el-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}
</style>

