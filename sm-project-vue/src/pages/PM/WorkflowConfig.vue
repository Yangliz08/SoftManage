<template>
  <div class="page-container">
    <div class="page-header"><h2>⚙️ 流程配置</h2></div>
    <div class="card-box">
      <h3 style="margin-bottom:16px">状态转移规则</h3>
      <el-table :data="rules" stripe v-loading="loading">
        <el-table-column prop="fromState" label="源状态" width="150">
          <template #default="{ row }">{{ STATE_MAP[row.fromState] || row.fromState }}</template>
        </el-table-column>
        <el-table-column label="" width="60"><template #default><el-icon><Right /></el-icon></template></el-table-column>
        <el-table-column prop="toState" label="目标状态" width="150">
          <template #default="{ row }">{{ STATE_MAP[row.toState] || row.toState }}</template>
        </el-table-column>
        <el-table-column prop="actionName" label="动作" width="120" />
        <el-table-column prop="actionCode" label="动作编码" width="160" />
        <el-table-column prop="requiredRole" label="所需角色" width="100">
          <template #default="{ row }"><el-tag size="small">{{ row.requiredRole }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="isException" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isException ? 'danger' : 'success'" size="small">{{ row.isException ? '异常流' : '主干流' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { workflowApi } from '../../api/workflow.js'
import { STATE_MAP } from '../../utils/constants.js'

const rules = ref([])
const loading = ref(false)

async function loadRules() {
  loading.value = true
  try {
    const res = await workflowApi.getAllRules()
    rules.value = res.data || []
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(loadRules)
</script>

