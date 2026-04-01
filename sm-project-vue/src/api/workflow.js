import client from './client.js'

export const workflowApi = {
  transitionTask: (taskId, data) => client.post(`/workflow/tasks/${taskId}/transition`, data),
  blockTask: (taskId, data) => client.post(`/workflow/tasks/${taskId}/block`, data),
  unblockTask: (taskId, data) => client.post(`/workflow/tasks/${taskId}/unblock`, data),
  getTaskHistory: (taskId) => client.get(`/workflow/tasks/${taskId}/history`),
  getValidTransitions: (currentState) => client.get('/workflow/transitions', { params: { currentState } }),
  getAllRules: () => client.get('/workflow/rules'),
  getDefinition: () => client.get('/workflow/definition'),
  getClosureChains: (taskId) => client.get(`/workflow/tasks/${taskId}/closure-chains`)
}

