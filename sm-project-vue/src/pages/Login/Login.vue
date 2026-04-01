<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="40" color="#409EFF"><Monitor /></el-icon>
        <h1>SoftManage</h1>
        <p>软件项目管理系统</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="demo-accounts">
        <p>演示账号：</p>
        <el-space wrap>
          <el-tag @click="fillAccount('admin', 'admin123')" style="cursor:pointer">管理员 admin</el-tag>
          <el-tag type="success" @click="fillAccount('pm_user', 'admin123')" style="cursor:pointer">PM pm_user</el-tag>
          <el-tag type="warning" @click="fillAccount('dev_user', 'admin123')" style="cursor:pointer">DEV dev_user</el-tag>
          <el-tag type="danger" @click="fillAccount('test_user', 'admin123')" style="cursor:pointer">TEST test_user</el-tag>
        </el-space>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth.js'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function fillAccount(username, password) {
  form.username = username
  form.password = password
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    const defaultRoute = authStore.getDefaultRoute()
    router.push(defaultRoute)
  } catch (e) {
    // error already handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.login-header h1 {
  font-size: 28px;
  margin: 10px 0 4px;
  color: #303133;
}
.login-header p {
  color: #909399;
  font-size: 14px;
}
.demo-accounts {
  margin-top: 20px;
  text-align: center;
}
.demo-accounts p {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}
</style>

