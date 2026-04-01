import client from './client.js'

export const authApi = {
  login: (data) => client.post('/auth/login', data),
  logout: () => client.post('/auth/logout'),
  refreshToken: (refreshToken) => client.post('/auth/refresh', null, { params: { refreshToken } }),
  getUserInfo: () => client.get('/auth/user/info'),
  getUserPermissions: () => client.get('/auth/user/permissions'),
  getUserList: (params) => client.get('/auth/user/list', { params }),
  createUser: (data) => client.post('/auth/user/create', data),
  updateUserStatus: (userId, status) => client.put(`/auth/user/${userId}/status`, null, { params: { status } }),
  deleteUser: (userId) => client.delete(`/auth/user/${userId}`),
  getRoles: () => client.get('/auth/roles')
}

