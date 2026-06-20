import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref({})
  const role = ref('')
  const sidebarDisabled = ref(false)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const setRole = (newRole) => {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  const setSidebarDisabled = (disabled) => {
    sidebarDisabled.value = disabled
  }

  const clearUser = () => {
    token.value = ''
    userInfo.value = {}
    role.value = ''
    sidebarDisabled.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('role')
  }

  const initUser = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')
    const savedRole = localStorage.getItem('role')
    
    if (savedToken) {
      token.value = savedToken
    }
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
    }
    if (savedRole) {
      role.value = savedRole
    }
    // 初始化时重置 sidebarDisabled，只在考试页面时才设置为 true
    sidebarDisabled.value = false
  }

  return {
    token,
    userInfo,
    role,
    sidebarDisabled,
    setToken,
    setUserInfo,
    setRole,
    setSidebarDisabled,
    clearUser,
    initUser
  }
}, {
  // 不持久化 sidebarDisabled，只在内存中保存
  persist: {
    paths: ['token', 'userInfo', 'role']
  }
})