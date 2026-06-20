<template>
  <div class="teacher-info-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教师信息管理</span>
          <el-button type="primary" @click="handleAdd">新增教师</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="教师姓名">
          <el-input v-model="searchForm.name" placeholder="请输入教师姓名" clearable />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="searchForm.username" placeholder="请输入账号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 教师列表 -->
      <el-table :data="teacherList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="department" label="部门" width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
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
      <el-form :model="teacherForm" :rules="rules" ref="teacherFormRef" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="teacherForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="teacherForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!teacherForm.id">
          <el-input v-model="teacherForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="teacherForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="teacherForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="teacherForm.department" placeholder="请输入部门" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import teacherApi from '@/api/teacher'

const loading = ref(false)
const teacherList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  name: '',
  username: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增教师')
const teacherForm = ref({
  id: null,
  name: '',
  username: '',
  password: '',
  phone: '',
  email: '',
  department: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const teacherFormRef = ref(null)

onMounted(() => {
  loadTeacherList()
})

const loadTeacherList = async () => {
  loading.value = true
  try {
    const res = await teacherApi.search(searchForm.value)
    teacherList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadTeacherList()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    username: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadTeacherList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadTeacherList()
}

const handleAdd = () => {
  dialogTitle.value = '新增教师'
  teacherForm.value = {
    id: null,
    name: '',
    username: '',
    password: '',
    phone: '',
    email: '',
    department: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑教师'
  teacherForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await teacherFormRef.value.validate()
  loading.value = true
  try {
    if (teacherForm.value.id) {
      await teacherApi.update(teacherForm.value)
      ElMessage.success('更新成功')
    } else {
      await teacherApi.add(teacherForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTeacherList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该教师吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await teacherApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTeacherList()
  }).catch(() => {})
}
</script>

<style scoped>
.teacher-info-manage {
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