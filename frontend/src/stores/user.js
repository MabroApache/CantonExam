import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref({})
  const role = ref('')
  const sidebarDisabled = ref(false)

  const setToken = (newToken) => {
    token.value = newToken
    sessionStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    sessionStorage.setItem('userInfo', JSON.stringify(info))
  }

  const setRole = (newRole) => {
    role.value = newRole
    sessionStorage.setItem('role', newRole)
  }

  const setSidebarDisabled = (disabled) => {
    sidebarDisabled.value = disabled
  }

  const clearUser = () => {
    token.value = ''
    userInfo.value = {}
    role.value = ''
    sidebarDisabled.value = false
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    sessionStorage.removeItem('role')
  }

  const initUser = () => {
    const savedToken = sessionStorage.getItem('token')
    const savedUserInfo = sessionStorage.getItem('userInfo')
    const savedRole = sessionStorage.getItem('role')
    
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
  persist: {
    storage: sessionStorage,
    paths: ['token', 'userInfo', 'role']
  }
})