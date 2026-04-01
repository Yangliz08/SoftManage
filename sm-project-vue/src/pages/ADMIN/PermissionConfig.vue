<template>
  <div class="page-container">
    <div class="page-header"><h2>🔒 权限配置</h2></div>
    <div class="card-box">
      <el-alert title="查看各角色的菜单权限分配情况" type="info" :closable="false" style="margin-bottom:20px" />
      <el-tabs v-model="activeTab">
        <el-tab-pane label="PM - 项目经理" name="PM">
          <el-tree :data="pmMenus" :props="{ label: 'permName', children: 'children' }" show-checkbox default-expand-all />
        </el-tab-pane>
        <el-tab-pane label="DEV - 开发人员" name="DEV">
          <el-tree :data="devMenus" :props="{ label: 'permName', children: 'children' }" show-checkbox default-expand-all />
        </el-tab-pane>
        <el-tab-pane label="TEST - 测试人员" name="TEST">
          <el-tree :data="testMenus" :props="{ label: 'permName', children: 'children' }" show-checkbox default-expand-all />
        </el-tab-pane>
        <el-tab-pane label="ADMIN - 管理员" name="ADMIN">
          <el-tree :data="adminMenus" :props="{ label: 'permName', children: 'children' }" show-checkbox default-expand-all />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('PM')

const pmMenus = ref([
  { permName: 'PM系统', children: [
    { permName: '仪表板' }, { permName: '任务看板' }, { permName: '甘特图' },
    { permName: '流程配置' }, { permName: '需求管理' }, { permName: '任务分配' }
  ]}
])
const devMenus = ref([
  { permName: 'DEV系统', children: [
    { permName: '我的任务' }, { permName: '代码提交' }, { permName: '评审状态' }, { permName: '工作量统计' }
  ]}
])
const testMenus = ref([
  { permName: 'TEST系统', children: [
    { permName: '测试任务' }, { permName: '测试执行' }, { permName: '缺陷报告' }, { permName: '闭环验证' }
  ]}
])
const adminMenus = ref([
  { permName: 'ADMIN系统', children: [
    { permName: '发布审批' }, { permName: '用户管理' }, { permName: '权限配置' }, { permName: '系统日志' }
  ]}
])
</script>

