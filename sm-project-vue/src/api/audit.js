import client from './client.js'

export const auditApi = {
  getAuditLogs: (params) => client.get('/audit/logs', { params })
}

