<template>
  <div class="page-container">
    <div class="page-header"><h2>▶️ 测试执行</h2></div>
    <div class="card-box">
      <el-alert title="在此页面执行测试用例并记录结果" type="info" :closable="false" style="margin-bottom:20px" />
      <el-table :data="tasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }"><el-tag :type="row.priority === 'HIGH' ? 'danger' : 'info'" size="small">{{ row.priority }}</el-tag></template>
        </el-table-column>
        <el-table-column label="测试结果" width="200">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="success" size="small" @click="passTest(row)">通过</el-button>
              <el-button type="danger" size="small" @click="failTest(row)">失败</el-button>
            </el-button-group>
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

const tasks = ref([])
const loading = ref(false)

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getTaskList({ state: 'IN_TEST', page: 1, size: 50 })
    tasks.value = res.data?.records || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function passTest(task) {
  await ElMessageBox.confirm(`确认 ${task.taskCode} 测试通过？`, '确认')
  await workflowApi.transitionTask(task.id, { taskId: task.id, actionCode: 'PASS_TEST' })
  ElMessage.success('测试通过')
  loadTasks()
}

async function failTest(task) {
  const { value } = await ElMessageBox.prompt('请输入失败原因', '测试失败', { inputType: 'textarea' })
  await workflowApi.transitionTask(task.id, { taskId: task.id, actionCode: 'FAIL_TEST', remark: value })
  ElMessage.success('已标记失败')
  loadTasks()
}

onMounted(loadTasks)
</script>

