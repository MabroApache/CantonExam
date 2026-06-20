<template>
  <div class="course-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程信息管理</span>
          <el-button type="primary" @click="handleAdd">新增课程</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="授课教师">
          <el-select v-model="searchForm.teacherId" placeholder="请选择教师" clearable>
            <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.name" :value="teacher.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 课程列表 -->
      <el-table :data="courseList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="code" label="课程编码" width="150" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="description" label="课程描述" />
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
      <el-form :model="courseForm" :rules="rules" ref="courseFormRef" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程编码" prop="code">
          <el-input v-model="courseForm.code" placeholder="请输入课程编码" />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择教师" style="width: 100%">
            <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.name" :value="teacher.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="courseForm.credit" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
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
import courseApi from '@/api/course'
import teacherApi from '@/api/teacher'

const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const teachers = ref([])

const searchForm = ref({
  name: '',
  teacherId: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const courseForm = ref({
  id: null,
  name: '',
  code: '',
  teacherId: null,
  credit: 2,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入课程编码', trigger: 'blur' }],
  teacherId: [{ required: true, message: '请选择授课教师', trigger: 'change' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }]
}

const courseFormRef = ref(null)

onMounted(() => {
  loadCourseList()
  loadTeachers()
})

const loadCourseList = async () => {
  loading.value = true
  try {
    const res = await courseApi.search(searchForm.value)
    courseList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const loadTeachers = async () => {
  try {
    const res = await teacherApi.getList()
    teachers.value = res.data || []
  } catch (error) {
    console.error('加载教师失败', error)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadCourseList()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    teacherId: null
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadCourseList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadCourseList()
}

const handleAdd = () => {
  dialogTitle.value = '新增课程'
  courseForm.value = {
    id: null,
    name: '',
    code: '',
    teacherId: null,
    credit: 2,
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑课程'
  courseForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await courseFormRef.value.validate()
  loading.value = true
  try {
    if (courseForm.value.id) {
      await courseApi.update(courseForm.value)
      ElMessage.success('更新成功')
    } else {
      await courseApi.add(courseForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadCourseList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await courseApi.delete(row.id)
    ElMessage.success('删除成功')
    loadCourseList()
  }).catch(() => {})
}
</script>

<style scoped>
.course-manage {
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