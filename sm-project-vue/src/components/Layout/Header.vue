import { ref, computed, onMounted } from 'vue'
<template>
  <div class="header-container">
    <div class="header-left">
      <el-icon class="collapse-btn" @click="$emit('toggle-sidebar')"><Fold /></el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>{{ currentRoute.meta?.role || '' }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentRoute.meta?.title || '' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <!-- 通知 -->
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notify-badge">
        <el-icon class="header-icon" @click="showNotifications = true"><Bell /></el-icon>
      </el-badge>

      <!-- 用户下拉 -->
      <el-dropdown @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">{{ avatarText }}</el-avatar>
          <span class="user-name">{{ authStore.user?.realName || authStore.user?.username }}</span>
          <el-tag size="small" :type="roleTagType">{{ roleLabel }}</el-tag>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人信息</el-dropdown-item>
            <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 通知抽屉 -->
      <div v-loading="notifyLoading">
        <div v-if="notifications.length === 0 && !notifyLoading" class="empty-notify">
          <el-empty description="暂无通知" />
        </div>
        <div v-else>
          <el-button text type="primary" @click="handleReadAll" style="margin-bottom:12px">全部已读</el-button>
          <div v-for="item in notifications" :key="item.id" class="notify-item" :class="{ unread: !item.isRead }"
               @click="markItemRead(item)">
            <div class="notify-title">{{ item.title }}</div>
            <div class="notify-content">{{ item.content }}</div>
            <div class="notify-time">{{ item.createdAt }}</div>
          </div>
          <div class="notify-time">{{ item.createdAt }}</div>
        <el-empty description="暂无通知" />
      </div>
      <div v-else>
        <el-button text type="primary" @click="handleReadAll" style="margin-bottom:12px">全部已读</el-button>
        <div v-for="item in notifications" :key="item.id" class="notify-item" :class="{ unread: !item.isRead }">
          <div class="notify-title">{{ item.title }}</div>
import { ref, computed, onMounted, watch } from 'vue'
          <div class="notify-time">{{ item.createdAt }}</div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth.js'
import { notifyApi } from '../../api/notify.js'
import { ROLE_MAP } from '../../utils/constants.js'
const unreadCount = ref(const notifyLoading = ref(false)

// 打开抽屉时加载通知列表
watch(showNotifications, async (val) => {
  if (val) {
    await loadNotificationList()
  }
})

defineEmits(['toggle-sidebar'])

const router = useRouter()
const currentRoute = useRoute()
const authStore = useAuthStore()

const showNotifications = ref(false)
const notifications = ref([])

const avatarText = computed(() => {
  const name = authStore.user?.realName || authStore.user?.username || '?'
  return name.charAt(0)
})

const roleLabel = computed(() => {
  const role = authStore.primaryRole
  return ROLE_MAP[role]?.label || role
})

const roleTagType = computed(() => {
  const r = authStore.primaryRole
  if (r === 'ADMIN') return 'danger'
  if (r === 'PM') return ''
  if (r === 'DEV') return 'success'
  return 'warning'
async function loadNotificationList() {
  notifyLoading.value = true
  try {
    const res = await notifyApi.getNotifications({ page: 1, size: 20 })
    notifications.value = res.data?.records || []
  } catch (e) { /* ignore */ }
  finally { notifyLoading.value = false }
}

})

async function loadNotifications() {
  try {
    const res = await notifyApi.getUnreadCount()
    unreadCount.value = res.data || 0
async function markItemRead(item) {
  if (!item.isRead) {
    try {
      await notifyApi.markAsRead(item.id)
      item.isRead = 1
      if (unreadCount.value > 0) unreadCount.value--
    } catch (e) { /* ignore */ }
  }
}

  } catch (e) { /* ignore */ }
}

async function handleReadAll() {
  await notifyApi.markAllAsRead()
  unreadCount.value = 0
  notifications.value.forEach(n => n.isRead = 1)
async function handleCommand(cmd) {
  if (cmd === 'logout') {
    await authStore.logout()
    router.push('/login')
  }
}

onMounted(() => {
  loadNotifications()
  setInterval(loadNotifications, 60000)
})
</script>

<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.header-icon {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.notify-badge { cursor: pointer; }
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.user-name {
  font-size: 14px;
  color: #303133;
}
.notify-item {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.notify-item.unread {
  background: #ecf5ff;
}
.notify-title {
  font-weight: 600;
  margin-bottom: 4px;
}
.notify-content {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}
.notify-time {
  font-size: 12px;
  color: #909399;
}
</style>

