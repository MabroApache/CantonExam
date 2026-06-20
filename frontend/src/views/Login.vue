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
        
        <!-- 教师登录 -->
        <el-tab-pane label="教师登录" name="teacher">
          <el-form :model="teacherForm" :rules="rules" ref="teacherFormRef">
            <el-form-item prop="username">
              <el-input v-model="teacherForm.username" placeholder="请输入用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="teacherForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTeacherLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="text" @click="showRegisterDialog">没有账号？立即注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 学生登录 -->
        <el-tab-pane label="学生登录" name="student">
          <el-form :model="studentForm" :rules="rules" ref="studentFormRef">
            <el-form-item prop="username">
              <el-input v-model="studentForm.username" placeholder="请输入用户名/学号" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="studentForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleStudentLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="text" @click="showStudentRegisterDialog">没有账号？立即注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="login-footer">
        <p>© 2024 广交会在线考试系统</p>
      </div>
    </div>
    
    <!-- 教师注册对话框 -->
    <el-dialog title="教师注册" v-model="registerVisible" width="500px">
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
        <el-form-item label="所属部门">
          <el-input v-model="registerForm.department" placeholder="请输入所属部门" />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="registerForm.title" placeholder="请输入职称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
      </template>
    </el-dialog>
    
    <!-- 学生注册对话框 -->
    <el-dialog title="学生注册" v-model="studentRegisterVisible" width="500px">
      <el-form :model="studentRegisterForm" :rules="studentRegisterRules" ref="studentRegisterFormRef">
        <el-form-item label="用户名/学号" prop="username">
          <el-input v-model="studentRegisterForm.username" placeholder="请输入用户名/学号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="studentRegisterForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentRegisterForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="studentRegisterForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="studentRegisterForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="studentRegisterForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="studentRegisterForm.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-input v-model="studentRegisterForm.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="studentRegisterForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="studentRegisterForm.college" placeholder="请输入学院" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="studentRegisterVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStudentRegister" :loading="loading">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import adminApi from '@/api/admin'
import teacherApi from '@/api/teacher'
import studentApi from '@/api/student'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('admin')
const loading = ref(false)
const registerVisible = ref(false)
const studentRegisterVisible = ref(false)

// 管理员登录表单
const adminForm = ref({
  username: '',
  password: ''
})

// 教师登录表单
const teacherForm = ref({
  username: '',
  password: ''
})

// 学生登录表单
const studentForm = ref({
  username: '',
  password: ''
})

// 教师注册表单
const registerForm = ref({
  username: '',
  password: '',
  name: '',
  gender: '1',
  phone: '',
  email: '',
  department: '',
  title: ''
})

// 学生注册表单
const studentRegisterForm = ref({
  username: '',
  password: '',
  name: '',
  gender: '1',
  phone: '',
  email: '',
  studentNo: '',
  className: '',
  major: '',
  college: ''
})

// 登录验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册验证规则
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

const studentRegisterRules = {
  username: [{ required: true, message: '请输入用户名/学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级', trigger: 'blur' }]
}

const adminFormRef = ref(null)
const teacherFormRef = ref(null)
const studentFormRef = ref(null)
const registerFormRef = ref(null)
const studentRegisterFormRef = ref(null)

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

// 教师登录
const handleTeacherLogin = async () => {
  await teacherFormRef.value.validate()
  loading.value = true
  try {
    const res = await teacherApi.login(teacherForm.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.teacher)
    userStore.setRole('teacher')
    ElMessage.success('登录成功')
    router.push('/teacher/home')
  } finally {
    loading.value = false
  }
}

// 学生登录
const handleStudentLogin = async () => {
  await studentFormRef.value.validate()
  loading.value = true
  try {
    const res = await studentApi.login(studentForm.value)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.student)
    userStore.setRole('student')
    ElMessage.success('登录成功')
    router.push('/student/home')
  } finally {
    loading.value = false
  }
}

// 显示教师注册对话框
const showRegisterDialog = () => {
  registerVisible.value = true
}

// 显示学生注册对话框
const showStudentRegisterDialog = () => {
  studentRegisterVisible.value = true
}

// 教师注册
const handleRegister = async () => {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await teacherApi.register(registerForm.value)
    ElMessage.success('注册成功，请登录')
    registerVisible.value = false
    registerForm.value = {
      username: '',
      password: '',
      name: '',
      gender: '1',
      phone: '',
      email: '',
      department: '',
      title: ''
    }
  } finally {
    loading.value = false
  }
}

// 学生注册
const handleStudentRegister = async () => {
  await studentRegisterFormRef.value.validate()
  loading.value = true
  try {
    await studentApi.register(studentRegisterForm.value)
    ElMessage.success('注册成功，请等待管理员审核后登录')
    studentRegisterVisible.value = false
    studentRegisterForm.value = {
      username: '',
      password: '',
      name: '',
      gender: '1',
      phone: '',
      email: '',
      studentNo: '',
      className: '',
      major: '',
      college: ''
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