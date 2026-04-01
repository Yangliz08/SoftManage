<template>
  <div class="page-container">
    <div class="page-header">
      <h2>📄 需求管理</h2>
      <el-button type="primary" @click="showDialog = true"><el-icon><Plus /></el-icon>新建需求</el-button>
    </div>
    <div class="card-box">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="reqCode" label="编码" width="180" />
        <el-table-column prop="reqName" label="需求名称" min-width="200" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.priority === 'HIGH' ? 'danger' : row.priority === 'MEDIUM' ? 'warning' : 'info'" size="small">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="editReq(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="deleteReq(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-16" background layout="prev, pager, next" :total="total" :page-size="10" @current-change="loadList" />
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑需求' : '新建需求'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.reqName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority">
            <el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveReq">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { taskApi } from '../../api/task.js'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const showDialog = ref(false)
const editingId = ref(null)
const form = reactive({ reqName: '', description: '', priority: 'MEDIUM', projectId: 1 })

async function loadList(page = 1) {
  loading.value = true
  try {
    const res = await taskApi.getRequirementList({ projectId: 1, page, size: 10 })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function editReq(row) {
  editingId.value = row.id
  Object.assign(form, { reqName: row.reqName, description: row.description, priority: row.priority })
  showDialog.value = true
}

async function saveReq() {
  try {
    if (editingId.value) {
      await taskApi.updateRequirement(editingId.value, form)
    } else {
      await taskApi.createRequirement(form)
    }
    ElMessage.success('保存成功')
    showDialog.value = false
    editingId.value = null
    loadList()
  } catch (e) { /* handled */ }
}

async function deleteReq(id) {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  await taskApi.deleteRequirement(id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(() => loadList())
</script>

<style scoped>
.mt-16 { margin-top: 16px; }
</style>

