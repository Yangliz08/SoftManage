<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <Sidebar :isCollapse="isCollapse" />
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <Header @toggle-sidebar="isCollapse = !isCollapse" />
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import Header from './Header.vue'
import Sidebar from './Sidebar.vue'

const isCollapse = ref(false)
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-aside {
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.layout-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.layout-main {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>

