<template>
  <div class="student-info">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生信息管理</span>
          <el-button type="primary" @click="handleAdd">新增学生</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="searchForm.className" placeholder="请输入班级" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 学生列表 -->
      <el-table :data="studentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleAudit(row)" v-if="row.status === 0">审核</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" style="color: #F56C6C">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="studentForm" :rules="rules" ref="studentFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="studentForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!studentForm.id">
          <el-input v-model="studentForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="studentForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="studentForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="studentForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="studentForm.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-input v-model="studentForm.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="studentForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="studentForm.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="studentForm.status">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 审核对话框 -->
    <el-dialog title="审核学生" v-model="auditVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="学生姓名">
          <el-input :value="currentStudent.name" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditStatus">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSave" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import studentApi from '@/api/student'

const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  username: '',
  name: '',
  className: '',
  status: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增学生')
const studentForm = ref({
  id: null,
  username: '',
  password: '',
  name: '',
  gender: '1',
  phone: '',
  email: '',
  studentNo: '',
  className: '',
  major: '',
  college: '',
  status: 0
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级', trigger: 'blur' }]
}

const studentFormRef = ref(null)

const auditVisible = ref(false)
const currentStudent = ref({})
const auditStatus = ref('1')

onMounted(() => {
  loadStudentList()
})

const loadStudentList = async () => {
  loading.value = true
  try {
    const res = await studentApi.search(searchForm.value)
    studentList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadStudentList()
}

const handleReset = () => {
  searchForm.value = {
    username: '',
    name: '',
    className: '',
    status: null
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadStudentList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadStudentList()
}

const handleAdd = () => {
  dialogTitle.value = '新增学生'
  studentForm.value = {
    id: null,
    username: '',
    password: '',
    name: '',
    gender: '1',
    phone: '',
    email: '',
    studentNo: '',
    className: '',
    major: '',
    college: '',
    status: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑学生'
  studentForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await studentFormRef.value.validate()
  loading.value = true
  try {
    if (studentForm.value.id) {
      await studentApi.update(studentForm.value)
      ElMessage.success('更新成功')
    } else {
      await studentApi.add(studentForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadStudentList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await studentApi.delete(row.id)
    ElMessage.success('删除成功')
    loadStudentList()
  }).catch(() => {})
}

const handleAudit = (row) => {
  currentStudent.value = row
  auditStatus.value = '1'
  auditVisible.value = true
}

const handleAuditSave = async () => {
  loading.value = true
  try {
    await studentApi.audit({ id: currentStudent.value.id, status: parseInt(auditStatus.value) })
    ElMessage.success('审核成功')
    auditVisible.value = false
    loadStudentList()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.student-info {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>