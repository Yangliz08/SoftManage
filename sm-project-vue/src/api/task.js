import client from './client.js'

export const taskApi = {
  // 任务 CRUD
  createTask: (data) => client.post('/tasks', data),
  getTaskDetail: (taskId) => client.get(`/tasks/${taskId}`),
  updateTask: (taskId, data) => client.put(`/tasks/${taskId}`, data),
  deleteTask: (taskId) => client.delete(`/tasks/${taskId}`),

  // 任务操作
  assignTask: (taskId, assigneeId) => client.post(`/tasks/${taskId}/assign`, { assigneeId }),
  startDevelopment: (taskId) => client.post(`/tasks/${taskId}/start-dev`),
  submitForReview: (taskId, codeUrl) => client.post(`/tasks/${taskId}/submit-review`, { codeUrl }),

  // 看板 & 列表
  getBoardData: (projectId) => client.get('/tasks/board/data', { params: { projectId } }),
  getTaskList: (params) => client.get('/tasks/list', { params }),
  getMyTasks: () => client.get('/tasks/my-tasks'),
  getTaskStatus: (taskId) => client.get(`/tasks/${taskId}/status`),

  // 项目
  createProject: (data) => client.post('/tasks/projects', data),
  getProject: (id) => client.get(`/tasks/projects/${id}`),
  updateProject: (id, data) => client.put(`/tasks/projects/${id}`, data),
  deleteProject: (id) => client.delete(`/tasks/projects/${id}`),
  getProjectList: (params) => client.get('/tasks/projects/list', { params }),
  getMyProjects: () => client.get('/tasks/projects/my'),

  // 需求
  createRequirement: (data) => client.post('/tasks/requirements', data),
  getRequirement: (id) => client.get(`/tasks/requirements/${id}`),
  updateRequirement: (id, data) => client.put(`/tasks/requirements/${id}`, data),
  deleteRequirement: (id) => client.delete(`/tasks/requirements/${id}`),
  getRequirementList: (params) => client.get('/tasks/requirements/list', { params })
}

