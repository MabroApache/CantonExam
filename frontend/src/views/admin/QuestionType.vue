<template>
  <div class="question-type-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题型信息管理</span>
          <el-button type="primary" @click="handleAdd">新增题型</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="题型名称">
          <el-input v-model="searchForm.name" placeholder="请输入题型名称" clearable />
        </el-form-item>
        <el-form-item label="题型编码">
          <el-input v-model="searchForm.code" placeholder="请输入题型编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 题型列表 -->
      <el-table :data="typeList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="题型名称" />
        <el-table-column prop="code" label="题型编码" width="150" />
        <el-table-column prop="description" label="描述" />
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="typeForm" :rules="rules" ref="typeFormRef" label-width="100px">
        <el-form-item label="题型名称" prop="name">
          <el-input v-model="typeForm.name" placeholder="请输入题型名称" />
        </el-form-item>
        <el-form-item label="题型编码" prop="code">
          <el-select v-model="typeForm.code" placeholder="请选择题型编码" style="width: 100%">
            <el-option label="单选题" value="single" />
            <el-option label="多选题" value="multi" />
            <el-option label="判断题" value="judge" />
            <el-option label="填空题" value="fill" />
            <el-option label="简答题" value="essay" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="typeForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import questionTypeApi from '@/api/questionType'

const loading = ref(false)
const typeList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  name: '',
  code: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增题型')
const typeForm = ref({
  id: null,
  name: '',
  code: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入题型名称', trigger: 'blur' }],
  code: [{ required: true, message: '请选择题型编码', trigger: 'change' }]
}

const typeFormRef = ref(null)

onMounted(() => {
  loadTypeList()
})

const loadTypeList = async () => {
  loading.value = true
  try {
    const res = await questionTypeApi.search(searchForm.value)
    typeList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadTypeList()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    code: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadTypeList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadTypeList()
}

const handleAdd = () => {
  dialogTitle.value = '新增题型'
  typeForm.value = {
    id: null,
    name: '',
    code: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑题型'
  typeForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await typeFormRef.value.validate()
  loading.value = true
  try {
    if (typeForm.value.id) {
      await questionTypeApi.update(typeForm.value)
      ElMessage.success('更新成功')
    } else {
      await questionTypeApi.add(typeForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTypeList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该题型吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await questionTypeApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTypeList()
  }).catch(() => {})
}
</script>

<style scoped>
.question-type-manage {
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