import client from './client.js'

export const notifyApi = {
  getNotifications: (params) => client.get('/notify/list', { params }),
  markAsRead: (id) => client.post(`/notify/${id}/read`),
  markAllAsRead: () => client.post('/notify/read-all'),
  getUnreadCount: () => client.get('/notify/unread-count'),
  deleteNotification: (id) => client.delete(`/notify/${id}`)
}

