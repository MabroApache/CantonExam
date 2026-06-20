<template>
  <div class="teacher-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="infoForm" :rules="infoRules" ref="infoFormRef" label-width="100px" style="max-width: 500px">
            <el-form-item label="姓名">
              <el-input v-model="infoForm.name" disabled />
            </el-form-item>
            <el-form-item label="账号">
              <el-input v-model="infoForm.username" disabled />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="infoForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="部门">
              <el-input v-model="infoForm.department" placeholder="请输入部门" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo" :loading="loading">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" style="max-width: 500px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="loading">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import teacherApi from '@/api/teacher'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const activeTab = ref('info')

const infoForm = ref({
  id: null,
  name: '',
  username: '',
  phone: '',
  email: '',
  department: ''
})

const infoRules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const infoFormRef = ref(null)

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const passwordFormRef = ref(null)

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = () => {
  infoForm.value = {
    id: userInfo.value.id,
    name: userInfo.value.name,
    username: userInfo.value.username,
    phone: userInfo.value.phone || '',
    email: userInfo.value.email || '',
    department: userInfo.value.department || ''
  }
}

const handleUpdateInfo = async () => {
  await infoFormRef.value.validate()
  loading.value = true
  try {
    await teacherApi.update(infoForm.value)
    ElMessage.success('更新成功')
    // 更新用户信息
    userStore.setUserInfo(infoForm.value)
  } finally {
    loading.value = false
  }
}

const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate()
  loading.value = true
  try {
    await teacherApi.updatePassword({
      id: userInfo.value.id,
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.teacher-profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>