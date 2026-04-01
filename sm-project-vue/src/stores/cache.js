import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 看板缓存 Store - 仅用于看板数据
 */
export const useCacheStore = defineStore('cache', () => {
  const BOARD_TTL = 5 * 60 * 1000 // 5 分钟
  const boardData = ref(null)
  const boardTimestamp = ref(null)

  const isBoardCacheValid = computed(() => {
    if (!boardData.value || !boardTimestamp.value) return false
    return (Date.now() - boardTimestamp.value) <= BOARD_TTL
  })

  const cacheTimeRemaining = computed(() => {
    if (!isBoardCacheValid.value) return 0
    return Math.max(0, Math.floor((BOARD_TTL - (Date.now() - boardTimestamp.value)) / 1000))
  })

  const cacheExpirePercent = computed(() => {
    if (!isBoardCacheValid.value) return 0
    return (cacheTimeRemaining.value / (BOARD_TTL / 1000)) * 100
  })

  function getBoardCache() {
    if (isBoardCacheValid.value) return boardData.value
    return null
  }

  function setBoardCache(data) {
    boardData.value = data
    boardTimestamp.value = Date.now()
  }

  function clearBoardCache() {
    boardData.value = null
    boardTimestamp.value = null
  }

  return {
    boardData, boardTimestamp,
    isBoardCacheValid, cacheTimeRemaining, cacheExpirePercent,
    getBoardCache, setBoardCache, clearBoardCache
  }
})

