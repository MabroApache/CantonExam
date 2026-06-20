<template>
  <div class="share-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>交流分享管理</span>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 分享列表 -->
      <el-table :data="shareList" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="studentName" label="发布学生" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
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
    
    <!-- 查看分享详情 -->
    <el-dialog title="分享详情" v-model="viewDialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题" :span="2">{{ viewShare.title }}</el-descriptions-item>
        <el-descriptions-item label="发布学生">{{ viewShare.studentName }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ viewShare.createTime }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ viewShare.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ viewShare.likeCount }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div v-html="viewShare.content" class="share-content"></div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import shareApi from '@/api/share'

const loading = ref(false)
const shareList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  title: '',
  studentName: ''
})

const viewDialogVisible = ref(false)
const viewShare = ref({})

onMounted(() => {
  loadShareList()
})

const loadShareList = async () => {
  loading.value = true
  try {
    const res = await shareApi.search(searchForm.value)
    shareList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadShareList()
}

const handleReset = () => {
  searchForm.value = {
    title: '',
    studentName: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadShareList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadShareList()
}

const handleView = (row) => {
  viewShare.value = row
  viewDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该分享吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await shareApi.delete(row.id)
    ElMessage.success('删除成功')
    loadShareList()
  }).catch(() => {})
}
</script>

<style scoped>
.share-manage {
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

.share-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  line-height: 1.6;
}

.share-content img {
  max-width: 100%;
  height: auto;
}
</style>