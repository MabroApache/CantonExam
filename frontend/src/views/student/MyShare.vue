<template>
  <div class="my-share">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的分享</span>
          <el-button type="primary" @click="handleAdd">新增分享</el-button>
        </div>
      </template>
      
      <!-- 分享列表 -->
      <el-table :data="shareList" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
      <el-form :model="shareForm" :rules="rules" ref="shareFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="shareForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="shareForm.content" type="textarea" :rows="10" placeholder="请输入分享内容" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import shareApi from '@/api/share'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const shareList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const dialogTitle = ref('新增分享')
const shareForm = ref({
  id: null,
  title: '',
  content: '',
  studentId: null
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入分享内容', trigger: 'blur' }]
}

const shareFormRef = ref(null)

onMounted(() => {
  loadShareList()
})

const loadShareList = async () => {
  loading.value = true
  try {
    const res = await shareApi.getByStudentId(userInfo.value.id)
    shareList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadShareList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadShareList()
}

const handleAdd = () => {
  dialogTitle.value = '新增分享'
  shareForm.value = {
    id: null,
    title: '',
    content: '',
    studentId: userInfo.value.id
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑分享'
  shareForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await shareFormRef.value.validate()
  loading.value = true
  try {
    if (shareForm.value.id) {
      await shareApi.update(shareForm.value)
      ElMessage.success('更新成功')
    } else {
      await shareApi.add(shareForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadShareList()
  } finally {
    loading.value = false
  }
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
.my-share {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>