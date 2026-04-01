<template>
  <div class="page-container">
    <div class="page-header"><h2>📊 仪表板</h2></div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="icon" style="background:#ecf5ff;color:#409EFF"><el-icon><List /></el-icon></div>
        <div class="info"><h3>{{ dashboard.totalTasks || 0 }}</h3><p>总任务数</p></div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background:#f0f9eb;color:#67C23A"><el-icon><CircleCheck /></el-icon></div>
        <div class="info"><h3>{{ dashboard.completedCount || 0 }}</h3><p>已完成</p></div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background:#fdf6ec;color:#E6A23C"><el-icon><Loading /></el-icon></div>
        <div class="info"><h3>{{ dashboard.devCount || 0 }}</h3><p>开发中</p></div>
      </div>
      <div class="stat-card">
        <div class="icon" style="background:#fef0f0;color:#F56C6C"><el-icon><Warning /></el-icon></div>
        <div class="info"><h3>{{ dashboard.blockedCount || 0 }}</h3><p>已阻塞</p></div>
      </div>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="card-box">
          <h3 style="margin-bottom:16px">任务状态分布</h3>
          <div ref="pieChartRef" style="height:300px"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card-box">
          <h3 style="margin-bottom:16px">异常统计</h3>
          <div class="exception-stats">
            <div class="ex-item"><span>评审驳回</span><el-tag type="warning">{{ dashboard.reviewRejectCount || 0 }} 次</el-tag></div>
            <div class="ex-item"><span>测试失败</span><el-tag type="danger">{{ dashboard.testFailCount || 0 }} 次</el-tag></div>
            <div class="ex-item"><span>阻塞次数</span><el-tag type="info">{{ dashboard.blockCount || 0 }} 次</el-tag></div>
            <div class="ex-item"><span>平均修复时长</span><el-tag>{{ dashboard.avgFixHours || 0 }} 小时</el-tag></div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { reportApi } from '../../api/report.js'
import * as echarts from 'echarts'
import { STATE_MAP, STATE_COLORS } from '../../utils/constants.js'

const dashboard = ref({})
const pieChartRef = ref()

async function loadDashboard() {
  try {
    const res = await reportApi.getDashboard(1) // 默认项目ID=1
    dashboard.value = res.data || {}
    nextTick(() => renderPieChart())
  } catch (e) { /* ignore */ }
}

function renderPieChart() {
  if (!pieChartRef.value) return
  const chart = echarts.init(pieChartRef.value)
  const dist = dashboard.value.stateDistribution || {}
  const data = Object.entries(dist).map(([key, val]) => ({
    name: STATE_MAP[key] || key,
    value: val,
    itemStyle: { color: STATE_COLORS[key] }
  }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data,
      label: { formatter: '{b}: {c}' }
    }]
  })
}

onMounted(() => loadDashboard())
</script>

<style scoped>
.exception-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px 0;
}
.ex-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
}
</style>

