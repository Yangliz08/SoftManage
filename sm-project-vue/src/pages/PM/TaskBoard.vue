<template>
  <div class="page-container">
    <div class="page-header">
      <h2>📋 任务看板</h2>
      <div style="display:flex;gap:10px;align-items:center">
        <!-- 缓存信息 -->
        <div v-if="cacheStore.isBoardCacheValid" class="cache-info">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>缓存 {{ cacheStore.cacheTimeRemaining }}秒</span>
          <el-progress :percentage="cacheStore.cacheExpirePercent" :show-text="false" :stroke-width="4" style="width:80px" />
        </div>
        <el-button type="primary" :loading="loading" @click="refreshBoard">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <el-skeleton v-if="loading && boardData.length === 0" :rows="5" animated />

    <div class="board-grid" v-else>
      <div v-for="col in columns" :key="col.state" class="board-column">
        <div class="column-header">
          <span class="col-dot" :style="{ background: col.color }"></span>
          <h3>{{ col.title }}</h3>
          <el-tag size="small" round>{{ getTasksByState(col.state).length }}</el-tag>
        </div>
        <div class="column-body">
          <div v-for="task in getTasksByState(col.state)" :key="task.id" class="task-card" @click="showTaskDetail(task)">
            <div class="card-top">
              <el-tag size="small" :color="col.color" effect="dark" style="border:none;font-size:11px">{{ col.title }}</el-tag>
              <span class="task-code">{{ task.taskCode }}</span>
            </div>
            <h4>{{ task.taskName }}</h4>
            <el-progress :percentage="task.progress || 0" :stroke-width="6" />
            <div class="card-bottom">
              <el-tag :type="priorityType(task.priority)" size="small">{{ task.priority }}</el-tag>
              <span class="assignee">{{ task.assigneeName || '未分配' }}</span>
            </div>
          </div>
          <el-empty v-if="getTasksByState(col.state).length === 0" description="暂无任务" :image-size="60" />
        </div>
      </div>
    </div>

    <!-- 任务详情对话框 -->
    <el-dialog v-model="detailVisible" :title="selectedTask?.taskCode" width="600px">
      <el-descriptions :column="2" border v-if="selectedTask">
        <el-descriptions-item label="任务名称" :span="2">{{ selectedTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">{{ STATE_MAP[selectedTask.currentState] }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ selectedTask.priority }}</el-descriptions-item>
        <el-descriptions-item label="进度">{{ selectedTask.progress }}%</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ selectedTask.assigneeName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ selectedTask.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { taskApi } from '../../api/task.js'
import { useCacheStore } from '../../stores/cache.js'
import { STATE_MAP, STATE_COLORS } from '../../utils/constants.js'

const cacheStore = useCacheStore()
const boardData = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const selectedTask = ref(null)

const columns = [
  { state: 'PLANNING', title: '需求管理', color: STATE_COLORS.PLANNING },
  { state: 'ASSIGNED', title: '任务准备', color: STATE_COLORS.ASSIGNED },
  { state: 'IN_DEVELOPMENT', title: '开发中', color: STATE_COLORS.IN_DEVELOPMENT },
  { state: 'PENDING_REVIEW', title: '待评审', color: STATE_COLORS.PENDING_REVIEW },
  { state: 'IN_TEST', title: '测试中', color: STATE_COLORS.IN_TEST },
  { state: 'PENDING_RELEASE', title: '待发布', color: STATE_COLORS.PENDING_RELEASE },
  { state: 'RELEASED', title: '已上线', color: STATE_COLORS.RELEASED },
  { state: 'COMPLETED', title: '已完成', color: STATE_COLORS.COMPLETED }
]

function getTasksByState(state) {
  return boardData.value.filter(t => t.currentState === state)
}

function priorityType(p) {
  if (p === 'HIGH' || p === 'URGENT') return 'danger'
  if (p === 'MEDIUM') return 'warning'
  return 'info'
}

function showTaskDetail(task) {
  selectedTask.value = task
  detailVisible.value = true
}

async function refreshBoard() {
  loading.value = true
  try {
    cacheStore.clearBoardCache()
    const res = await taskApi.getBoardData(1)
    boardData.value = res.data || []
    cacheStore.setBoardCache(boardData.value)
    ElMessage.success('看板已刷新')
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(async () => {
  const cached = cacheStore.getBoardCache()
  if (cached) {
    boardData.value = cached
  } else {
    await refreshBoard()
  }
})
</script>

<style scoped>
.cache-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
  padding: 6px 12px;
  background: #f0f9ff;
  border-radius: 6px;
}
.board-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.board-column {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  max-height: 75vh;
}
.column-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  border-bottom: 2px solid #f0f0f0;
}
.col-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}
.column-header h3 {
  flex: 1;
  font-size: 14px;
  font-weight: 600;
}
.column-body {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
}
.task-card {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.task-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.task-code { font-size: 11px; color: #909399; }
.task-card h4 {
  font-size: 13px;
  margin-bottom: 8px;
  line-height: 1.4;
}
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.assignee { font-size: 12px; color: #909399; }
</style>

