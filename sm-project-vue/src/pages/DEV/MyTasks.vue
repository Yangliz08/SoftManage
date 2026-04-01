<template>
  <div class="page-container">
    <div class="page-header"><h2>📝 我的任务</h2></div>
    <div class="card-box">
      <el-table :data="tasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="currentState" label="状态" width="110">
          <template #default="{ row }"><el-tag size="small">{{ STATE_MAP[row.currentState] || row.currentState }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }"><el-tag :type="row.priority === 'HIGH' ? 'danger' : 'info'" size="small">{{ row.priority }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="140">
          <template #default="{ row }"><el-progress :percentage="row.progress || 0" /></template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.currentState === 'ASSIGNED'" text type="primary" size="small" @click="startDev(row)">开始开发</el-button>
            <el-button v-if="row.currentState === 'IN_DEVELOPMENT'" text type="success" size="small" @click="submitReview(row)">提交评审</el-button>
            <el-button text size="small" @click="viewHistory(row)">历史</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 历史对话框 -->
    <el-dialog v-model="historyVisible" title="状态历史" width="600px">
      <el-timeline>
        <el-timeline-item v-for="h in history" :key="h.id" :timestamp="h.createdAt" placement="top">
          <el-card shadow="never">{{ STATE_MAP[h.fromState] }} → {{ STATE_MAP[h.toState] }} <span style="color:#909399;margin-left:8px">{{ h.remark || '' }}</span></el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>

    <!-- 提交评审弹窗 -->
    <el-dialog v-model="submitVisible" title="提交代码评审" width="450px">
      <el-form label-width="80px">
        <el-form-item label="PR链接"><el-input v-model="codeUrl" placeholder="https://github.com/..." /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" @click="doSubmitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { taskApi } from '../../api/task.js'
import { workflowApi } from '../../api/workflow.js'
import { STATE_MAP } from '../../utils/constants.js'

const tasks = ref([])
const loading = ref(false)
const historyVisible = ref(false)
const history = ref([])
const submitVisible = ref(false)
const codeUrl = ref('')
const currentTaskId = ref(null)

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getMyTasks()
    tasks.value = res.data || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function startDev(task) {
  await taskApi.startDevelopment(task.id)
  ElMessage.success('已开始开发')
  loadTasks()
}

function submitReview(task) {
  currentTaskId.value = task.id
  codeUrl.value = ''
  submitVisible.value = true
}

async function doSubmitReview() {
  if (!codeUrl.value) return ElMessage.warning('请输入PR链接')
  await taskApi.submitForReview(currentTaskId.value, codeUrl.value)
  ElMessage.success('已提交评审')
  submitVisible.value = false
  loadTasks()
}

async function viewHistory(task) {
  try {
    const res = await workflowApi.getTaskHistory(task.id)
    history.value = res.data || []
    historyVisible.value = true
  } catch (e) { /* handled */ }
}

onMounted(loadTasks)
</script>

