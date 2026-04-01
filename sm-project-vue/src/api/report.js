import client from './client.js'

export const reportApi = {
  getDashboard: (projectId) => client.get('/report/dashboard', { params: { projectId } }),
  getProgressTrend: (projectId, period = 'DAILY', days = 30) => client.get('/report/progress/trend', { params: { projectId, period, days } }),
  getTeamWorkload: (projectId, startDate, endDate) => client.get('/report/workload/team', { params: { projectId, startDate, endDate } }),
  getUserWorkload: (projectId) => client.get('/report/workload/user', { params: { projectId } }),
  getExceptionTrend: (projectId, days = 30) => client.get('/report/exceptions/trend', { params: { projectId, days } }),
  getGanttData: (projectId) => client.get('/report/gantt', { params: { projectId } })
}

