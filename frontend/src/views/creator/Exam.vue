<template>
  <div class="creator-exam">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考试安排管理</span>
          <el-button type="primary" @click="handleAdd">新增考试</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="考试名称">
          <el-input v-model="searchForm.name" placeholder="请输入考试名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 考试列表 -->
      <el-table :data="examList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="考试名称" />
        <el-table-column prop="paperName" label="试卷" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">
              {{ getStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
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
      <el-form :model="examForm" :rules="rules" ref="examFormRef" label-width="100px">
        <el-form-item label="考试名称" prop="name">
          <el-input v-model="examForm.name" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="试卷" prop="paperId">
          <el-select v-model="examForm.paperId" placeholder="请选择试卷" style="width: 100%">
            <el-option v-for="paper in papers" :key="paper.id" :label="paper.name" :value="paper.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="examForm.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="examForm.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="examForm.duration" :min="1" :max="300" />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>
        <el-form-item label="考试说明">
          <el-input v-model="examForm.description" type="textarea" :rows="3" placeholder="请输入考试说明" />
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
import examApi from '@/api/exam'
import paperApi from '@/api/paper'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const examList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const papers = ref([])

const searchForm = ref({
  name: '',
  status: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增考试')
const examForm = ref({
  id: null,
  name: '',
  paperId: null,
  startTime: '',
  endTime: '',
  duration: 60,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  paperId: [{ required: true, message: '请选择试卷', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

const examFormRef = ref(null)

onMounted(() => {
  loadExamList()
  loadPapers()
})

const loadExamList = async () => {
  loading.value = true
  try {
    const res = await examApi.search(searchForm.value)
    let list = res.data || []

    if (searchForm.value.status !== null && searchForm.value.status !== undefined) {
      list = list.filter(e => getCurrentStatus(e) === searchForm.value.status)
    }

    examList.value = list
    total.value = examList.value.length || 0
  } finally {
    loading.value = false
  }
}

const loadPapers = async () => {
  try {
    const res = await paperApi.getList()
    papers.value = res.data || []
  } catch (error) {
    console.error('加载试卷失败', error)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadExamList()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    status: null
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadExamList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadExamList()
}

const handleAdd = () => {
  dialogTitle.value = '新增考试'
  examForm.value = {
    id: null,
    name: '',
    paperId: null,
    startTime: '',
    endTime: '',
    duration: 60,
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑考试'
  examForm.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await examFormRef.value.validate()
  loading.value = true
  try {
    const formData = { ...examForm.value }
    if (formData.startTime) {
      formData.startTime = formatDateTime(formData.startTime)
    }
    if (formData.endTime) {
      formData.endTime = formatDateTime(formData.endTime)
    }

    formData.creatorId = userInfo.value.id
    formData.creatorName = userInfo.value.name

    const paper = papers.value.find(p => p.id === formData.paperId)
    if (paper) {
      formData.paperName = paper.name
      formData.totalScore = paper.totalScore
    }

    if (examForm.value.id) {
      await examApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await examApi.add(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadExamList()
  } finally {
    loading.value = false
  }
}

// 格式化日期时间为 yyyy-MM-dd HH:mm:ss
const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 根据当前时间计算考试状态
const getCurrentStatus = (row) => {
  const now = new Date()
  const startTime = new Date(row.startTime)
  const endTime = new Date(row.endTime)

  if (now < startTime) {
    return 0 // 未开始
  } else if (now >= startTime && now <= endTime) {
    return 1 // 进行中
  } else {
    return 2 // 已结束
  }
}

const getStatusText = (row) => {
  const status = getCurrentStatus(row)
  return status === 0 ? '未开始' : status === 1 ? '进行中' : '已结束'
}

const getStatusType = (row) => {
  const status = getCurrentStatus(row)
  return status === 0 ? 'info' : status === 1 ? 'success' : 'danger'
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该考试吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await examApi.delete(row.id)
    ElMessage.success('删除成功')
    loadExamList()
  }).catch(() => {})
}
</script>

<style scoped>
.creator-exam {
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