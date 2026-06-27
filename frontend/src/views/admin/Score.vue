<template>
  <div class="score-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩信息管理</span>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="考试名称">
          <el-input v-model="searchForm.examName" placeholder="请输入考试名称" clearable />
        </el-form-item>
        <el-form-item label="考生姓名">
          <el-input v-model="searchForm.candidateName" placeholder="请输入考生姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 成绩列表 -->
      <el-table :data="scoreList" style="width: 100%" v-loading="loading">
        <el-table-column prop="examName" label="考试名称" />
        <el-table-column prop="candidateName" label="考生姓名" width="120" />
        <el-table-column prop="totalScore" label="总分" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.totalScore >= 60 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ row.totalScore }}分
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="objectiveScore" label="客观题得分" width="120" />
        <el-table-column prop="subjectiveScore" label="主观题得分" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">查看详情</el-button>
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
    
    <!-- 查看成绩详情 -->
    <el-dialog title="成绩详情" v-model="viewDialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="考试名称">{{ viewScore.examName }}</el-descriptions-item>
        <el-descriptions-item label="考生姓名">{{ viewScore.candidateName }}</el-descriptions-item>
        <el-descriptions-item label="总分">
          <span :style="{ color: viewScore.totalScore >= 60 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
            {{ viewScore.totalScore }}分
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="客观题得分">{{ viewScore.objectiveScore }}分</el-descriptions-item>
        <el-descriptions-item label="主观题得分">{{ viewScore.subjectiveScore }}分</el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">{{ viewScore.submitTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider>答题详情</el-divider>
      
      <div v-for="(answer, index) in answerList" :key="answer.id" class="answer-item">
        <div class="answer-title">
          <span class="answer-number">{{ index + 1 }}.</span>
          <span>{{ answer.title }}</span>
          <span class="answer-type">[{{ answer.typeName }}]</span>
          <span class="answer-score">({{ answer.score }}分)</span>
        </div>
        <div v-if="answer.optionA" class="answer-options">
          <div>A. {{ answer.optionA }}</div>
          <div>B. {{ answer.optionB }}</div>
          <div>C. {{ answer.optionC }}</div>
          <div>D. {{ answer.optionD }}</div>
        </div>
        <div class="answer-content">
          <p><strong>考生答案：</strong>{{ answer.candidateAnswer || '未作答' }}</p>
          <p><strong>正确答案：</strong>{{ answer.correctAnswer }}</p>
          <p>
            <strong>得分：</strong>
            <span :style="{ color: answer.getScore >= answer.score ? '#67C23A' : '#F56C6C' }">
              {{ answer.getScore }}分
            </span>
          </p>
        </div>
        <div v-if="answer.analysis" class="answer-analysis">
          <strong>解析：</strong>{{ answer.analysis }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import scoreApi from '@/api/score'
import recordApi from '@/api/record'

const loading = ref(false)
const scoreList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  examName: '',
  candidateName: ''
})

const viewDialogVisible = ref(false)
const viewScore = ref({})
const answerList = ref([])

onMounted(() => {
  loadScoreList()
})

const loadScoreList = async () => {
  loading.value = true
  try {
    const res = await scoreApi.search(searchForm.value)
    scoreList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadScoreList()
}

const handleReset = () => {
  searchForm.value = {
    examName: '',
    candidateName: ''
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadScoreList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadScoreList()
}

const handleView = async (row) => {
  viewScore.value = row
  try {
    const res = await recordApi.getByExamId(row.examId, row.candidateId)
    answerList.value = res.data || []
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取答题详情失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该成绩记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await scoreApi.delete(row.id)
    ElMessage.success('删除成功')
    loadScoreList()
  }).catch(() => {})
}
</script>

<style scoped>
.score-manage {
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

.answer-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}

.answer-title {
  margin-bottom: 10px;
  font-size: 15px;
}

.answer-number {
  font-weight: bold;
  margin-right: 5px;
}

.answer-type {
  color: #409EFF;
  margin-left: 10px;
}

.answer-score {
  color: #E6A23C;
  margin-left: 10px;
}

.answer-options {
  margin-left: 20px;
  margin-bottom: 10px;
  color: #606266;
}

.answer-options div {
  margin: 5px 0;
}

.answer-content {
  margin-top: 10px;
}

.answer-content p {
  margin: 8px 0;
}

.answer-analysis {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
</style>