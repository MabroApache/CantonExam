<template>
  <div class="admin-profile">
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      
      <el-form :model="profileForm" :rules="rules" ref="profileFormRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input :value="userInfo.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="profileForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="头像">
          <el-avatar :size="100" :src="profileForm.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">更新信息</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card style="margin-top: 20px">
      <template #header>
        <span>修改密码</span>
      </template>
      
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdatePassword" :loading="loading">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import adminApi from '@/api/admin'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)

const profileForm = ref({
  id: null,
  name: '',
  phone: '',
  email: '',
  avatar: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

const profileFormRef = ref(null)

const passwordForm = ref({
  id: null,
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
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const passwordFormRef = ref(null)

onMounted(() => {
  profileForm.value = {
    id: userInfo.value.id,
    name: userInfo.value.name,
    phone: userInfo.value.phone,
    email: userInfo.value.email,
    avatar: userInfo.value.avatar
  }
  passwordForm.value.id = userInfo.value.id
})

const handleUpdate = async () => {
  await profileFormRef.value.validate()
  loading.value = true
  try {
    await adminApi.update(profileForm.value)
    userStore.setUserInfo(profileForm.value)
    ElMessage.success('更新成功')
  } finally {
    loading.value = false
  }
}

const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate()
  loading.value = true
  try {
    await adminApi.updatePassword(passwordForm.value)
    ElMessage.success('密码修改成功')
    passwordForm.value = {
      id: userInfo.value.id,
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
.admin-profile {
  padding: 20px;
}
</style>