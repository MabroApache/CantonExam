<template>
  <div class="paper-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>试卷信息管理</span>
          <el-button type="primary" @click="handleAdd">新增试卷</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="试卷名称">
          <el-input v-model="searchForm.name" placeholder="请输入试卷名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 试卷列表 -->
      <el-table :data="paperList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="试卷名称" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="questionCount" label="题目数量" width="100" />
        <el-table-column prop="duration" label="考试时长(分钟)" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
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
      <el-form :model="paperForm" :rules="rules" ref="paperFormRef" label-width="100px">
        <el-form-item label="试卷名称" prop="name">
          <el-input v-model="paperForm.name" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="paperForm.duration" :min="1" :max="300" />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>
        <el-form-item label="总分" prop="totalScore" v-if="!isEdit">
          <el-input-number v-model="paperForm.totalScore" :min="1" :max="500" />
          <span style="margin-left: 10px">分</span>
        </el-form-item>
        <el-form-item label="试卷说明">
          <el-input v-model="paperForm.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看试卷详情 -->
    <el-dialog title="试卷详情" v-model="viewDialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="试卷名称">{{ viewPaper.name }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ viewPaper.totalScore }}分</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ viewPaper.duration }}分钟</el-descriptions-item>
        <el-descriptions-item label="试卷说明" :span="2">{{ viewPaper.description || '无' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider>题目列表</el-divider>
      
      <div v-for="(question, index) in paperQuestions" :key="question.id" class="question-item">
        <div class="question-title">
          <span class="question-number">{{ index + 1 }}.</span>
          <span>{{ question.title }}</span>
          <span class="question-type">[{{ question.typeName }}]</span>
          <span class="question-score">({{ question.score }}分)</span>
        </div>
        <div v-if="question.optionA" class="question-options">
          <div>A. {{ question.optionA }}</div>
          <div>B. {{ question.optionB }}</div>
          <div>C. {{ question.optionC }}</div>
          <div>D. {{ question.optionD }}</div>
        </div>
        <div class="question-answer">
          <strong>答案：</strong>{{ question.answer }}
        </div>
        <div v-if="question.analysis" class="question-analysis">
          <strong>解析：</strong>{{ question.analysis }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import paperApi from '@/api/paper'

const loading = ref(false)
const paperList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  name: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增试卷')
const paperForm = ref({
  id: null,
  name: '',
  duration: 120,
  totalScore: 100,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }]
}

const paperFormRef = ref(null)

const viewDialogVisible = ref(false)
const viewPaper = ref({})
const paperQuestions = ref([])

onMounted(() => {
  loadPaperList()
})

const loadPaperList = async () => {
  loading.value = true
  try {
    const res = await paperApi.search(searchForm.value)
    paperList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadPaperList()
}

const handleReset = () => {
  searchForm.value = {
    name: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadPaperList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadPaperList()
}

const handleAdd = () => {
  dialogTitle.value = '新增试卷'
  paperForm.value = {
    id: null,
    name: '',
    duration: 120,
    totalScore: 100,
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑试卷'
  paperForm.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  viewPaper.value = row
  try {
    const res = await paperApi.getQuestions(row.id)
    paperQuestions.value = res.data || []
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取试卷题目失败')
  }
}

const handleSave = async () => {
  await paperFormRef.value.validate()
  loading.value = true
  try {
    if (paperForm.value.id) {
      await paperApi.update(paperForm.value)
      ElMessage.success('更新成功')
    } else {
      await paperApi.add(paperForm.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadPaperList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该试卷吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await paperApi.delete(row.id)
    ElMessage.success('删除成功')
    loadPaperList()
  }).catch(() => {})
}
</script>

<style scoped>
.paper-manage {
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

.question-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}

.question-title {
  margin-bottom: 10px;
  font-size: 15px;
}

.question-number {
  font-weight: bold;
  margin-right: 5px;
}

.question-type {
  color: #409EFF;
  margin-left: 10px;
}

.question-score {
  color: #E6A23C;
  margin-left: 10px;
}

.question-options {
  margin-left: 20px;
  margin-bottom: 10px;
  color: #606266;
}

.question-options div {
  margin: 5px 0;
}

.question-answer {
  margin-top: 10px;
  color: #67C23A;
}

.question-analysis {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
</style>