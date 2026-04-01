<template>
  <div class="page-container">
    <div class="page-header"><h2>📤 代码提交</h2></div>
    <div class="card-box">
      <el-alert title="选择一个开发中的任务，提交代码评审" type="info" :closable="false" style="margin-bottom:20px" />
      <el-table :data="devTasks" stripe v-loading="loading">
        <el-table-column prop="taskCode" label="编码" width="180" />
        <el-table-column prop="taskName" label="任务名称" min-width="200" />
        <el-table-column prop="progress" label="进度" width="140">
          <template #default="{ row }"><el-progress :percentage="row.progress || 0" /></template>
        </el-table-column>
        <el-table-column prop="codeUrl" label="代码链接" min-width="200">
          <template #default="{ row }">
            <a v-if="row.codeUrl" :href="row.codeUrl" target="_blank" style="color:#409EFF">{{ row.codeUrl }}</a>
            <span v-else style="color:#909399">未提交</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openSubmit(row)">提交评审</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="提交代码评审" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="任务">{{ form.taskName }}</el-form-item>
        <el-form-item label="PR / 代码链接">
          <el-input v-model="form.codeUrl" placeholder="https://github.com/your-repo/pull/123" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="提交说明（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="doSubmit">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { taskApi } from '../../api/task.js'

const tasks = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ taskId: null, taskName: '', codeUrl: '', remark: '' })

const devTasks = computed(() => tasks.value.filter(t => t.currentState === 'IN_DEVELOPMENT'))

async function loadTasks() {
  loading.value = true
  try {
    const res = await taskApi.getMyTasks()
    tasks.value = res.data || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function openSubmit(task) {
  form.taskId = task.id
  form.taskName = task.taskName
  form.codeUrl = task.codeUrl || ''
  form.remark = ''
  dialogVisible.value = true
}

async function doSubmit() {
  if (!form.codeUrl) return ElMessage.warning('请输入代码链接')
  submitting.value = true
  try {
    await taskApi.submitForReview(form.taskId, form.codeUrl)
    ElMessage.success('代码已提交评审')
    dialogVisible.value = false
    loadTasks()
  } catch (e) { /* handled */ }
  finally { submitting.value = false }
}

onMounted(loadTasks)
</script>

