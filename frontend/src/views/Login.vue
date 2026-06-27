<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>广交会在线考试系统</h2>
        <p>Canton Fair Online Exam System</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 管理员登录 -->
        <el-tab-pane label="管理员登录" name="admin">
          <el-form :model="adminForm" :rules="rules" ref="adminFormRef">
            <el-form-item prop="username">
              <el-input v-model="adminForm.username" placeholder="请输入用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAdminLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 出题人登录 -->
        <el-tab-pane label="出题人登录" name="creator">
          <el-form :model="creatorForm" :rules="rules" ref="creatorFormRef">
            <el-form-item prop="username">
              <el-input v-model="creatorForm.username" placeholder="请输入用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="creatorForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCreatorLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="text" @click="showRegisterDialog">没有账号？立即注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 考生登录 -->
        <el-tab-pane label="考生登录" name="candidate">
          <el-form :model="candidateForm" :rules="rules" ref="candidateFormRef">
            <el-form-item prop="username">
              <el-input v-model="candidateForm.username" placeholder="请输入用户名/学号" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="candidateForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCandidateLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="text" @click="showCandidateRegisterDialog">没有账号？立即注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-footer">
        <p>© 2024 广交会在线考试系统</p>
      </div>
    </div>

    <!-- 出题人注册对话框 -->
    <el-dialog title="出题人注册" v-model="registerVisible" width="500px">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="registerForm.departmentId" placeholder="请选择所属部门" style="width: 100%">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
      </template>
    </el-dialog>

    <!-- 考生注册对话框 -->
    <el-dialog title="考生注册" v-model="candidateRegisterVisible" width="500px">
      <el-form :model="candidateRegisterForm" :rules="candidateRegisterRules" ref="candidateRegisterFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="candidateRegisterForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="candidateRegisterForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="candidateRegisterForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="candidateRegisterForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="candidateRegisterForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="candidateRegisterForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="candidateRegisterVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCandidateRegister" :loading="loading">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import adminApi from '@/api/admin'
import creatorApi from '@/api/creator'
import candidateApi from '@/api/candidate'
import departmentApi from '@/api/department'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('admin')
const loading = ref(false)
const registerVisible = ref(false)
const candidateRegisterVisible = ref(false)
const departmentList = ref([])

// 管理员登录表单
const adminForm = ref({
  username: '',
  password: ''
})

// 出题人登录表单
const creatorForm = ref({
  username: '',
  password: ''
})

// 考生登录表单
const candidateForm = ref({
  username: '',
  password: ''
})

// 出题人注册表单
const registerForm = ref({
  username: '',
  password: '',
  name: '',
  gender: '1',
  phone: '',
  email: '',
  departmentId: null
})

// 考生注册表单
const candidateRegisterForm = ref({
  username: '',
  password: '',
  name: '',
  gender: '1',
  phone: '',
  email: ''
})

// 登录验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 出题人注册验证规则
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
}

// 考生注册验证规则
const candidateRegisterRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const adminFormRef = ref(null)
const creatorFormRef = ref(null)
const candidateFormRef = ref(null)
const registerFormRef = ref(null)
const candidateRegisterFormRef = ref(null)

onMounted(async () => {
  await loadDepartmentList()
})

const loadDepartmentList = async () => {
  try {
    const res = await departmentApi.getList()
    departmentList.value = res.data || []
  } catch (error) {
    console.error('加载部门列表失败', error)
  }
}

// 管理员登录
const handleAdminLogin = async () => {
  await adminFormRef.value.validate()
  loading.value = true
  try {
    const res = await adminApi.login(adminForm.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.admin)
    userStore.setRole('admin')
    ElMessage.success('登录成功')
    router.push('/admin/home')
  } finally {
    loading.value = false
  }
}

// 出题人登录
const handleCreatorLogin = async () => {
  await creatorFormRef.value.validate()
  loading.value = true
  try {
    const res = await creatorApi.login(creatorForm.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.creator)
    userStore.setRole('creator')
    ElMessage.success('登录成功')
    router.push('/creator/home')
  } finally {
    loading.value = false
  }
}

// 考生登录
const handleCandidateLogin = async () => {
  await candidateFormRef.value.validate()
  loading.value = true
  try {
    const res = await candidateApi.login(candidateForm.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.candidate)
    userStore.setRole('candidate')
    ElMessage.success('登录成功')
    router.push('/candidate/home')
  } finally {
    loading.value = false
  }
}

// 显示出题人注册对话框
const showRegisterDialog = () => {
  registerVisible.value = true
}

// 显示考生注册对话框
const showCandidateRegisterDialog = () => {
  candidateRegisterVisible.value = true
}

// 出题人注册
const handleRegister = async () => {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await creatorApi.register(registerForm.value)
    ElMessage.success('注册成功，您可以立即登录')
    registerVisible.value = false
    registerForm.value = {
      username: '',
      password: '',
      name: '',
      gender: '1',
      phone: '',
      email: '',
      departmentId: null
    }
  } finally {
    loading.value = false
  }
}

// 考生注册
const handleCandidateRegister = async () => {
  await candidateRegisterFormRef.value.validate()
  loading.value = true
  try {
    await candidateApi.register(candidateRegisterForm.value)
    ElMessage.success('注册成功，您可以立即登录')
    candidateRegisterVisible.value = false
    candidateRegisterForm.value = {
      username: '',
      password: '',
      name: '',
      gender: '1',
      phone: '',
      email: ''
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 450px;
  background: #fff;
  border-radius: 10px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}
</style>