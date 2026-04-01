<template>
  <div class="page-container">
    <div class="page-header"><h2>👁️ 评审状态</h2></div>
    <div class="card-box">
      <el-table :data="reviewTasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="currentState" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.currentState === 'PENDING_REVIEW' ? 'warning' : row.currentState === 'IN_TEST' ? 'success' : 'info'" size="small">
              {{ STATE_MAP[row.currentState] || row.currentState }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="codeUrl" label="PR链接" min-width="200">
          <template #default="{ row }">
            <a v-if="row.codeUrl" :href="row.codeUrl" target="_blank" style="color:#409EFF">{{ row.codeUrl }}</a>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="reviewerName" label="评审人" width="100">
          <template #default="{ row }">{{ row.reviewerName || '待指定' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text size="small" @click="viewHistory(row)">查看历史</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="historyVisible" title="评审历史" width="600px">
      <el-timeline>
        <el-timeline-item v-for="h in history" :key="h.id" :timestamp="h.createdAt" placement="top">
          <el-card shadow="never">
            <strong>{{ STATE_MAP[h.fromState] }} → {{ STATE_MAP[h.toState] }}</strong>
            <p v-if="h.remark" style="color:#909399;margin-top:4px">{{ h.remark }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { taskApi } from '../../api/task.js'
import { workflowApi } from '../../api/workflow.js'
import { STATE_MAP } from '../../utils/constants.js'

const tasks = ref([])
const loading = ref(false)
const historyVisible = ref(false)
const history = ref([])

const reviewTasks = computed(() =>
  tasks.value.filter(t => ['PENDING_REVIEW', 'IN_TEST', 'IN_DEVELOPMENT'].includes(t.currentState))
)

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getMyTasks()
    tasks.value = res.data || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
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

