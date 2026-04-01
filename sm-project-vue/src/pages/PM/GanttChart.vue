<template>
  <div class="page-container">
    <div class="page-header"><h2>📅 甘特图</h2></div>
    <div class="card-box">
      <div ref="ganttRef" style="height:500px"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { reportApi } from '../../api/report.js'

const ganttRef = ref()

async function loadGantt() {
  try {
    const res = await reportApi.getGanttData(1)
    const tasks = res.data || []
    renderGantt(tasks)
  } catch (e) { renderGantt([]) }
}

function renderGantt(tasks) {
  if (!ganttRef.value) return
  const chart = echarts.init(ganttRef.value)
  const categories = tasks.map(t => t.taskName)
  const data = tasks.map((t, i) => ({
    name: t.taskName,
    value: [i, new Date(t.plannedStart || '2026-01-01').getTime(), new Date(t.plannedEnd || '2026-01-15').getTime(), t.progress || 0],
    itemStyle: { color: t.progress >= 100 ? '#67C23A' : '#409EFF' }
  }))
  chart.setOption({
    tooltip: { formatter: p => `${p.name}<br/>进度: ${p.value[3]}%` },
    grid: { left: 200, right: 40, top: 20, bottom: 40 },
    xAxis: { type: 'time' },
    yAxis: { type: 'category', data: categories, inverse: true },
    series: [{ type: 'bar', data, encode: { x: [1, 2], y: 0 }, barMaxWidth: 20 }]
  })
}

onMounted(loadGantt)
</script>

