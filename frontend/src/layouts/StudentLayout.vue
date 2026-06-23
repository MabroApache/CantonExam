<template>
  <div class="layout-container">
    <el-container>
      <!-- 考试期间隐藏侧边栏 -->
      <el-aside v-if="!sidebarDisabled" width="200px" class="aside">
        <div class="logo">
          <h3>学生系统</h3>
        </div>
        <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
          <el-menu-item index="/student/home">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
          <el-menu-item index="/student/exam">
            <el-icon><Calendar /></el-icon>
            <span>在线考试</span>
          </el-menu-item>
          <el-menu-item index="/student/score">
            <el-icon><TrendCharts /></el-icon>
            <span>我的成绩</span>
          </el-menu-item>
          <el-menu-item index="/student/profile">
            <el-icon><Setting /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container :class="{ 'exam-mode': sidebarDisabled }">
        <!-- 考试期间隐藏顶部导航 -->
        <el-header v-if="!sidebarDisabled" class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/student/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="30" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
                <span class="username">{{ userInfo.name || '学生' }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main" :class="{ 'exam-fullscreen': sidebarDisabled }">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')
const sidebarDisabled = computed(() => userStore.sidebarDisabled)

const goProfile = () => {
  router.push('/student/profile')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.clearUser()
    ElMessage.success('退出成功')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  height: 100vh;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  background-color: #263445;
}

.logo h3 {
  margin: 0;
}

.el-menu {
  border-right: none;
}

.header {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 10px;
  font-size: 14px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}

.main.exam-fullscreen {
  padding: 0;
  margin: 0;
}

.exam-mode {
  width: 100%;
}
</style>