const TOKEN_KEY = 'sm_access_token'
const REFRESH_TOKEN_KEY = 'sm_refresh_token'
const USER_KEY = 'sm_user_info'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

export function setRefreshToken(token) {
  localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

export function getUserInfo() {
  const info = localStorage.getItem(USER_KEY)
  return info ? JSON.parse(info) : null
}

export function setUserInfo(info) {
  localStorage.setItem(USER_KEY, JSON.stringify(info))
}

