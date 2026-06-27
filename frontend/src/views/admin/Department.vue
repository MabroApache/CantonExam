<template>
  <div class="department-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd">新增部门</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.name" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 部门列表 -->
      <el-table :data="departmentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="部门名称" />
        <el-table-column prop="description" label="部门描述" />
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
      <el-form :model="departmentForm" :rules="rules" ref="departmentFormRef" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="departmentForm.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门描述">
          <el-input v-model="departmentForm.description" type="textarea" :rows="3" placeholder="请输入部门描述" />
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
import departmentApi from '@/api/department'

const loading = ref(false)
const departmentList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  name: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const departmentForm = ref({
  id: null,
  name: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const departmentFormRef = ref(null)

onMounted(() => {
  loadDepartmentList()
})

const loadDepartmentList = async () => {
  loading.value = true
  try {
    const res = await departmentApi.search(searchForm.value)
    departmentList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadDepartmentList()
}

const handleReset = () => {
  searchForm.value = {
    name: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadDepartmentList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadDepartmentList()
}

const handleAdd = () => {
  dialogTitle.value = '新增部门'
  departmentForm.value = {
    id: null,
    name: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑部门'
  departmentForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await departmentFormRef.value.validate()
  loading.value = true
  try {
    if (departmentForm.value.id) {
      await departmentApi.update(departmentForm.value)
      ElMessage.success('更新成功')
    } else {
      await departmentApi.add(departmentForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadDepartmentList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await departmentApi.delete(row.id)
    ElMessage.success('删除成功')
    loadDepartmentList()
  }).catch(() => {})
}
</script>

<style scoped>
.department-manage {
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