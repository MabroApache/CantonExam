<template>
  <div class="notice-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统公告管理</span>
          <el-button type="primary" @click="handleAdd">新增公告</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="系统公告" :value="1" />
            <el-option label="考试通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 公告列表 -->
      <el-table :data="noticeList" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : 'success'">
              {{ row.type === 1 ? '系统公告' : '考试通知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'info' : 'success'">
              {{ row.status === 0 ? '草稿' : '已发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handlePublish(row)" v-if="row.status === 0">发布</el-button>
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
      <el-form :model="noticeForm" :rules="rules" ref="noticeFormRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="noticeForm.type">
            <el-option label="系统公告" :value="1" />
            <el-option label="考试通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="noticeForm.content" type="textarea" :rows="5" placeholder="请输入内容" />
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import noticeApi from '@/api/notice'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const noticeList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  title: '',
  type: null,
  status: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增公告')
const noticeForm = ref({
  id: null,
  title: '',
  type: 1,
  content: '',
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const noticeFormRef = ref(null)

onMounted(() => {
  loadNoticeList()
})

const loadNoticeList = async () => {
  loading.value = true
  try {
    const res = await noticeApi.search(searchForm.value)
    noticeList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadNoticeList()
}

const handleReset = () => {
  searchForm.value = {
    title: '',
    type: null,
    status: null
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadNoticeList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadNoticeList()
}

const handleAdd = () => {
  dialogTitle.value = '新增公告'
  noticeForm.value = {
    id: null,
    title: '',
    type: 1,
    content: '',
    status: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  noticeForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await noticeFormRef.value.validate()
  loading.value = true
  try {
    if (noticeForm.value.id) {
      await noticeApi.update(noticeForm.value)
      ElMessage.success('更新成功')
    } else {
      await noticeApi.add(noticeForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadNoticeList()
  } finally {
    loading.value = false
  }
}

const handlePublish = async (row) => {
  ElMessageBox.confirm('确定要发布该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await noticeApi.publish({ id: row.id, publisher: userInfo.value.name })
    ElMessage.success('发布成功')
    loadNoticeList()
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await noticeApi.delete(row.id)
    ElMessage.success('删除成功')
    loadNoticeList()
  }).catch(() => {})
}
</script>

<style scoped>
.notice-manage {
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