<template>
  <div class="page-container">
    <div class="page-header">
      <h2>👥 用户管理</h2>
      <el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon>新建用户</el-button>
    </div>
    <div class="card-box">
      <el-input v-model="keyword" placeholder="搜索用户名/姓名" style="width:300px;margin-bottom:16px" @keyup.enter="loadUsers()">
        <template #append><el-button @click="loadUsers()">搜索</el-button></template>
      </el-input>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="roles" label="角色" width="120">
          <template #default="{ row }">
            <el-tag v-for="r in row.roles" :key="r" size="small" style="margin-right:4px">{{ r }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button text :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button text type="danger" size="small" @click="deleteUser(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-16" background layout="prev, pager, next" :total="total" :page-size="10" @current-change="loadUsers" />
    </div>

    <el-dialog v-model="createVisible" title="新建用户" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleCode">
            <el-option label="项目经理(PM)" value="PM" />
            <el-option label="开发人员(DEV)" value="DEV" />
            <el-option label="测试人员(TEST)" value="TEST" />
            <el-option label="管理员(ADMIN)" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { authApi } from '../../api/auth.js'

const users = ref([])
const loading = ref(false)
const total = ref(0)
const keyword = ref('')
const createVisible = ref(false)
const form = reactive({ username: '', password: '', realName: '', email: '', phone: '', roleCode: 'DEV' })

async function loadUsers(page = 1) {
  loading.value = true
  try {
    const res = await authApi.getUserList({ page, size: 10, keyword: keyword.value })
    users.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

function openCreate() {
  Object.assign(form, { username: '', password: '', realName: '', email: '', phone: '', roleCode: 'DEV' })
  createVisible.value = true
}

async function doCreate() {
  if (!form.username || !form.password) return ElMessage.warning('用户名和密码不能为空')
  await authApi.createUser(form)
  ElMessage.success('创建成功')
  createVisible.value = false
  loadUsers()
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await authApi.updateUserStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadUsers()
}

async function deleteUser(id) {
  await ElMessageBox.confirm('确认删除该用户？', '警告', { type: 'warning' })
  await authApi.deleteUser(id)
  ElMessage.success('删除成功')
  loadUsers()
}

onMounted(() => loadUsers())
</script>

<style scoped>.mt-16 { margin-top: 16px; }</style>

