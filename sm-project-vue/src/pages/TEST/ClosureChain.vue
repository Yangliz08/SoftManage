<template>
  <div class="page-container">
    <div class="page-header"><h2>🔄 闭环验证</h2></div>
    <div class="card-box">
      <el-alert title="闭环验证：修复完成 → 评审通过 → 测试通过 → 闭环完成" type="info" :closable="false" style="margin-bottom:20px" />
      <el-input v-model="searchTaskId" placeholder="输入任务ID查询闭环链" style="width:300px;margin-bottom:16px">
        <template #append>
          <el-button @click="loadChains">查询</el-button>
        </template>
      </el-input>

      <el-table :data="chains" stripe v-loading="loading">
        <el-table-column prop="id" label="链ID" width="80" />
        <el-table-column prop="taskId" label="任务ID" width="100" />
        <el-table-column prop="chainStatus" label="链状态" width="130">
          <template #default="{ row }">
            <el-tag :type="row.chainStatus === 'CLOSED' ? 'success' : 'warning'" size="small">{{ row.chainStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="修复完成" width="100">
          <template #default="{ row }"><el-icon :color="row.step1FixDone ? '#67C23A' : '#dcdfe6'"><CircleCheck /></el-icon></template>
        </el-table-column>
        <el-table-column label="评审通过" width="100">
          <template #default="{ row }"><el-icon :color="row.step2ReviewPassed ? '#67C23A' : '#dcdfe6'"><CircleCheck /></el-icon></template>
        </el-table-column>
        <el-table-column label="测试通过" width="100">
          <template #default="{ row }"><el-icon :color="row.step3TestPassed ? '#67C23A' : '#dcdfe6'"><CircleCheck /></el-icon></template>
        </el-table-column>
        <el-table-column label="闭环完成" width="100">
          <template #default="{ row }"><el-icon :color="row.step4Closed ? '#67C23A' : '#dcdfe6'"><CircleCheck /></el-icon></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { workflowApi } from '../../api/workflow.js'

const searchTaskId = ref('')
const chains = ref([])
const loading = ref(false)

async function loadChains() {
  if (!searchTaskId.value) return ElMessage.warning('请输入任务ID')
  loading.value = true
  try {
    const res = await workflowApi.getClosureChains(searchTaskId.value)
    chains.value = res.data || []
    if (chains.value.length === 0) ElMessage.info('未找到闭环链记录')
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}
</script>

