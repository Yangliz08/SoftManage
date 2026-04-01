<template>
  <div class="page-container">
    <div class="page-header"><h2>🚀 发布审批</h2></div>
    <div class="card-box">
      <el-table :data="tasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="currentState" label="状态" width="110">
          <template #default="{ row }"><el-tag type="warning" size="small">{{ STATE_MAP[row.currentState] || row.currentState }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }"><el-tag :type="row.priority === 'HIGH' ? 'danger' : 'info'" size="small">{{ row.priority }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button v-if="row.currentState === 'PENDING_RELEASE'" type="success" size="small" @click="approveRelease(row)">审批通过</el-button>
            <el-button v-if="row.currentState === 'PENDING_RELEASE'" type="danger" size="small" @click="rejectRelease(row)">拒绝发布</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { taskApi } from '../../api/task.js'
import { workflowApi } from '../../api/workflow.js'
import { STATE_MAP } from '../../utils/constants.js'

const tasks = ref([])
const loading = ref(false)

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getTaskList({ state: 'PENDING_RELEASE', page: 1, size: 50 })
    tasks.value = res.data?.records || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function approveRelease(task) {
  await ElMessageBox.confirm(`确认审批通过 ${task.taskCode}？`, '发布审批')
  await workflowApi.transitionTask(task.id, { taskId: task.id, actionCode: 'APPROVE_RELEASE' })
  ElMessage.success('已审批通过')
  loadTasks()
}

async function rejectRelease(task) {
  const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝发布', { inputType: 'textarea' })
  await workflowApi.transitionTask(task.id, { taskId: task.id, actionCode: 'REJECT_RELEASE', remark: value })
  ElMessage.success('已拒绝发布')
  loadTasks()
}

onMounted(loadTasks)
</script>

