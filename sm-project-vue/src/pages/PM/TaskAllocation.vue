<template>
  <div class="page-container">
    <div class="page-header">
      <h2>👥 任务分配</h2>
      <el-button type="primary" @click="showCreateDialog = true"><el-icon><Plus /></el-icon>创建任务</el-button>
    </div>
    <div class="card-box">
      <el-table :data="tasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="任务编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="180" />
        <el-table-column prop="currentState" label="状态" width="110">
          <template #default="{ row }"><el-tag size="small">{{ STATE_MAP[row.currentState] || row.currentState }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }"><el-tag :type="row.priority === 'HIGH' ? 'danger' : 'info'" size="small">{{ row.priority }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="assigneeName" label="负责人" width="100">
          <template #default="{ row }">{{ row.assigneeName || '未分配' }}</template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="120">
          <template #default="{ row }"><el-progress :percentage="row.progress || 0" :stroke-width="6" /></template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openAssign(row)">分配</el-button>
            <el-button text type="success" size="small" @click="editTask(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="deleteTask(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-16" background layout="prev, pager, next" :total="total" :page-size="10" @current-change="loadTasks" />
    </div>

    <!-- 分配弹窗 -->
    <el-dialog v-model="assignVisible" title="分配任务" width="400px">
      <el-form label-width="80px">
        <el-form-item label="任务">{{ assigningTask?.taskName }}</el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="assigneeId" placeholder="输入用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="doAssign">确认分配</el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑任务弹窗 -->
    <el-dialog v-model="showCreateDialog" :title="editingTaskId ? '编辑任务' : '创建任务'" width="500px" @closed="resetForm">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="createForm.taskName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="createForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="createForm.priority">
            <el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTask">{{ editingTaskId ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { taskApi } from '../../api/task.js'
import { STATE_MAP } from '../../utils/constants.js'

const tasks = ref([])
const loading = ref(false)
const total = ref(0)
const assignVisible = ref(false)
const assigningTask = ref(null)
const assigneeId = ref('')
const showCreateDialog = ref(false)
const editingTaskId = ref(null)
const createForm = reactive({ taskName: '', description: '', priority: 'MEDIUM', projectId: 1 })

async function loadTasks(page = 1) {
  loading.value = true
  try {
    const res = await taskApi.getTaskList({ projectId: 1, page, size: 10 })
    tasks.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function openAssign(task) { assigningTask.value = task; assigneeId.value = ''; assignVisible.value = true }

async function doAssign() {
  if (!assigningTask.value?.id) return ElMessage.warning('请选择任务')
  const uid = Number(assigneeId.value)
  if (!uid || Number.isNaN(uid)) return ElMessage.warning('请输入有效的负责人ID')
  try {
    await taskApi.assignTask(assigningTask.value.id, uid)
    ElMessage.success('分配成功')
    assignVisible.value = false
    loadTasks()
  } catch (e) {
    ElMessage.error(e?.message || '分配失败')
  }
}

function resetForm() {
  editingTaskId.value = null
  createForm.taskName = ''
  createForm.description = ''
  createForm.priority = 'MEDIUM'
  createForm.projectId = 1
}

function editTask(row) {
  editingTaskId.value = row.id
  createForm.taskName = row.taskName || ''
  createForm.description = row.description || ''
  createForm.priority = row.priority || 'MEDIUM'
  createForm.projectId = row.projectId || 1
  showCreateDialog.value = true
}

async function saveTask() {
  if (!createForm.taskName?.trim()) return ElMessage.warning('请输入任务名称')
  try {
    if (editingTaskId.value) {
      await taskApi.updateTask(editingTaskId.value, createForm)
      ElMessage.success('保存成功')
    } else {
      await taskApi.createTask(createForm)
      ElMessage.success('创建成功')
    }
    showCreateDialog.value = false
    loadTasks()
  } catch (e) {
    ElMessage.error(e?.message || (editingTaskId.value ? '保存失败' : '创建失败'))
  }
}

async function deleteTask(id) {
  try {
    await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
    await taskApi.deleteTask(id)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(() => loadTasks())
</script>

<style scoped>.mt-16 { margin-top: 16px; }</style>
