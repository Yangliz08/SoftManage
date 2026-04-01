<template>
  <div class="page-container">
    <div class="page-header"><h2>📜 系统日志</h2></div>
    <div class="card-box">
      <el-form inline style="margin-bottom:16px">
        <el-form-item label="服务">
          <el-select v-model="filters.serviceName" clearable placeholder="全部" style="width:140px">
            <el-option label="认证服务" value="auth" />
            <el-option label="任务服务" value="task" />
            <el-option label="工作流" value="workflow" />
            <el-option label="通知服务" value="notification" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="filters.action" placeholder="操作类型" style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadLogs()">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="logs" stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="serviceName" label="服务" width="100" />
        <el-table-column prop="action" label="操作" width="160" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="userRole" label="角色" width="80" />
        <el-table-column prop="ipAddress" label="IP" width="130" />
        <el-table-column prop="status" label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executionTime" label="耗时(ms)" width="90" />
        <el-table-column prop="createdAt" label="时间" min-width="160" />
      </el-table>
      <el-pagination class="mt-16" background layout="prev, pager, next" :total="total" :page-size="20" @current-change="loadLogs" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { auditApi } from '../../api/audit.js'

const logs = ref([])
const loading = ref(false)
const total = ref(0)
const filters = reactive({ serviceName: '', action: '' })

async function loadLogs(page = 1) {
  loading.value = true
  try {
    const res = await auditApi.getAuditLogs({ ...filters, page, size: 20 })
    logs.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

onMounted(() => loadLogs())
</script>

<style scoped>.mt-16 { margin-top: 16px; }</style>

