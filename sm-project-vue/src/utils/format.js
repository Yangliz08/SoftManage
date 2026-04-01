import dayjs from 'dayjs'

/**
 * 格式化日期时间
 */
export function formatDateTime(date) {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化日期
 */
export function formatDate(date) {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 格式化相对时间
 */
export function formatTime(date) {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

