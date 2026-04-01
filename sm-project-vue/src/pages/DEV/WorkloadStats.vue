<template>
  <div class="page-container">
    <div class="page-header"><h2>📈 工作量统计</h2></div>
    <div class="stat-cards">
      <div class="stat-card">
        <div class="icon" style="background:#ecf5ff;color:#409EFF"><el-icon><List /></el-icon></div>
        <div class="info"><h3>{{ summary.total }}</h3><p>总任务数</p></div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background:#f0f9eb;color:#67C23A"><el-icon><CircleCheck /></el-icon></div>
        <div class="info"><h3>{{ summary.completed }}</h3><p>已完成</p></div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background:#fdf6ec;color:#E6A23C"><el-icon><Loading /></el-icon></div>
        <div class="info"><h3>{{ summary.inProgress }}</h3><p>进行中</p></div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background:#fef0f0;color:#F56C6C"><el-icon><Warning /></el-icon></div>
        <div class="info"><h3>{{ summary.blocked }}</h3><p>已阻塞</p></div>
      </div>
    </div>
    <div class="card-box">
      <h3 style="margin-bottom:16px">我的任务列表</h3>
      <el-table :data="tasks" stripe>
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="currentState" label="状态" width="110">
          <template #default="{ row }"><el-tag size="small">{{ STATE_MAP[row.currentState] || row.currentState }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="estimatedHours" label="预估工时" width="100" />
        <el-table-column prop="actualHours" label="实际工时" width="100" />
        <el-table-column prop="progress" label="进度" width="140">
          <template #default="{ row }"><el-progress :percentage="row.progress || 0" /></template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { taskApi } from '../../api/task.js'
import { STATE_MAP } from '../../utils/constants.js'

const tasks = ref([])

const summary = computed(() => ({
  total: tasks.value.length,
  completed: tasks.value.filter(t => t.currentState === 'COMPLETED').length,
  inProgress: tasks.value.filter(t => ['IN_DEVELOPMENT', 'PENDING_REVIEW', 'IN_TEST'].includes(t.currentState)).length,
  blocked: tasks.value.filter(t => t.isBlocked === 1).length
}))

async function loadTasks() {
  try {
    const res = await taskApi.getMyTasks()
    tasks.value = res.data || []
  } catch (e) { /* handled */ }
}

onMounted(loadTasks)
</script>

