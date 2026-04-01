<template>
  <div class="page-container">
    <div class="page-header"><h2>🧪 测试任务</h2></div>
    <div class="card-box">
      <el-table :data="tasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="currentState" label="状态" width="110">
          <template #default="{ row }"><el-tag :type="row.currentState === 'IN_TEST' ? 'warning' : 'info'" size="small">{{ STATE_MAP[row.currentState] || row.currentState }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }"><el-tag :type="row.priority === 'HIGH' ? 'danger' : 'info'" size="small">{{ row.priority }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button v-if="row.currentState === 'IN_TEST'" text type="success" size="small" @click="passTest(row)">测试通过</el-button>
            <el-button v-if="row.currentState === 'IN_TEST'" text type="danger" size="small" @click="failTest(row)">测试失败</el-button>
            <el-button text size="small" @click="viewHistory(row)">历史</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="failVisible" title="测试失败 - 填写原因" width="500px">
      <el-form label-width="80px">
        <el-form-item label="失败原因"><el-input v-model="failRemark" type="textarea" :rows="4" placeholder="请描述失败原因和缺陷详情" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="failVisible = false">取消</el-button>
        <el-button type="danger" @click="doFailTest">确认提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="historyVisible" title="状态历史" width="600px">
      <el-timeline>
        <el-timeline-item v-for="h in history" :key="h.id" :timestamp="h.createdAt" placement="top">
          <el-card shadow="never">{{ STATE_MAP[h.fromState] }} → {{ STATE_MAP[h.toState] }} <span style="color:#909399;margin-left:8px">{{ h.remark || '' }}</span></el-card>
        </el-timeline-item>
      </el-timeline>
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
const failVisible = ref(false)
const failRemark = ref('')
const currentTaskId = ref(null)
const historyVisible = ref(false)
const history = ref([])

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getTaskList({ state: 'IN_TEST', page: 1, size: 50 })
    tasks.value = res.data?.records || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function passTest(task) {
  await workflowApi.transitionTask(task.id, { taskId: task.id, actionCode: 'PASS_TEST', remark: '测试通过' })
  ElMessage.success('测试通过')
  loadTasks()
}

function failTest(task) {
  currentTaskId.value = task.id
  failRemark.value = ''
  failVisible.value = true
}

async function doFailTest() {
  if (!failRemark.value) return ElMessage.warning('请填写失败原因')
  await workflowApi.transitionTask(currentTaskId.value, { taskId: currentTaskId.value, actionCode: 'FAIL_TEST', remark: failRemark.value })
  ElMessage.success('已标记测试失败')
  failVisible.value = false
  loadTasks()
}

async function viewHistory(task) {
  const res = await workflowApi.getTaskHistory(task.id)
  history.value = res.data || []
  historyVisible.value = true
}

onMounted(loadTasks)
</script>

