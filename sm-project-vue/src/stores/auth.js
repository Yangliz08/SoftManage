import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/auth.js'
import { setToken, setRefreshToken, setUserInfo, removeToken, getToken, getUserInfo } from '../utils/token.js'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(getUserInfo())
  const token = ref(getToken())
  const roles = ref(user.value?.roles || [])
  const permissions = ref([])

  const isLoggedIn = computed(() => !!token.value)
  const primaryRole = computed(() => roles.value[0] || '')
  const isAdmin = computed(() => roles.value.includes('ADMIN'))
  const isPM = computed(() => roles.value.includes('PM'))
  const isDev = computed(() => roles.value.includes('DEV'))
  const isTest = computed(() => roles.value.includes('TEST'))
  const userId = computed(() => user.value?.userId || null)

  async function login(username, password) {
    const res = await authApi.login({ username, password })
    const data = res.data
    token.value = data.accessToken
    user.value = data
    roles.value = data.roles
    setToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    setUserInfo(data)
    return data
  }

  async function logout() {
    try {
      await authApi.logout()
    } catch (e) { /* ignore */ }
    token.value = null
    user.value = null
    roles.value = []
    permissions.value = []
    removeToken()
  }

  async function fetchUserInfo() {
    const res = await authApi.getUserInfo()
    user.value = { ...user.value, ...res.data }
    roles.value = res.data.roles || []
  }

  async function fetchPermissions() {
    const res = await authApi.getUserPermissions()
    permissions.value = res.data || []
  }

  function getDefaultRoute() {
    const role = primaryRole.value
    if (role === 'PM') return '/pm/dashboard'
    if (role === 'DEV') return '/dev/my-tasks'
    if (role === 'TEST') return '/test/test-tasks'
    if (role === 'ADMIN') return '/admin/user-mgmt'
    return '/login'
  }

  return {
    user, token, roles, permissions,
    isLoggedIn, primaryRole, isAdmin, isPM, isDev, isTest, userId,
    login, logout, fetchUserInfo, fetchPermissions, getDefaultRoute
  }
})

