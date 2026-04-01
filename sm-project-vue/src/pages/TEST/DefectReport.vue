<template>
  <div class="page-container">
    <div class="page-header"><h2>🐛 缺陷报告</h2></div>
    <div class="card-box">
      <el-alert title="查看测试失败和评审驳回产生的异常记录" type="warning" :closable="false" style="margin-bottom:20px" />
      <el-table :data="tasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="currentState" label="当前状态" width="120">
          <template #default="{ row }"><el-tag size="small">{{ STATE_MAP[row.currentState] || row.currentState }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="isBlocked" label="是否阻塞" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isBlocked" type="danger" size="small">已阻塞</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="blockReason" label="阻塞原因" min-width="200" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text size="small" @click="viewHistory(row)">历史</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="historyVisible" title="状态历史" width="600px">
      <el-timeline>
        <el-timeline-item v-for="h in history" :key="h.id" :timestamp="h.createdAt" placement="top" :type="h.actionCode?.includes('FAIL') || h.actionCode?.includes('REJECT') ? 'danger' : 'primary'">
          <el-card shadow="never">{{ STATE_MAP[h.fromState] }} → {{ STATE_MAP[h.toState] }} <span style="color:#909399;margin-left:8px">{{ h.remark || '' }}</span></el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { taskApi } from '../../api/task.js'
import { workflowApi } from '../../api/workflow.js'
import { STATE_MAP } from '../../utils/constants.js'

const tasks = ref([])
const loading = ref(false)
const historyVisible = ref(false)
const history = ref([])

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getTaskList({ page: 1, size: 50 })
    // 只显示有异常的任务
    tasks.value = (res.data?.records || []).filter(t => t.isBlocked || ['IN_DEVELOPMENT'].includes(t.currentState))
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function viewHistory(task) {
  const res = await workflowApi.getTaskHistory(task.id)
  history.value = res.data || []
  historyVisible.value = true
}

onMounted(loadTasks)
</script>

