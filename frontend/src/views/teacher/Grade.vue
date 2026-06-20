<template>
  <div class="teacher-grade">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>阅卷打分</span>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="考试名称">
          <el-input v-model="searchForm.examName" placeholder="请输入考试名称" clearable />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="阅卷状态">
          <el-select v-model="searchForm.gradeStatus" placeholder="请选择阅卷状态" clearable>
            <el-option label="未阅卷" :value="0" />
            <el-option label="已阅卷" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 待阅卷列表 -->
      <el-table :data="recordList" style="width: 100%" v-loading="loading">
        <el-table-column prop="examName" label="考试名称" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="objectiveScore" label="客观题得分" width="120" />
        <el-table-column prop="subjectiveScore" label="主观题得分" width="120">
          <template #default="{ row }">
            <span v-if="row.subjectiveScore">{{ row.subjectiveScore }}分</span>
            <span v-else style="color: #E6A23C">待阅卷</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="100">
          <template #default="{ row }">
            <span v-if="row.totalScore" :style="{ color: row.totalScore >= 60 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
              {{ row.totalScore }}分
            </span>
            <span v-else style="color: #E6A23C">待阅卷</span>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleGrade(row)" v-if="!row.subjectiveScore">阅卷</el-button>
            <el-button type="text" size="small" @click="handleView(row)" v-else>查看</el-button>
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
    
    <!-- 阅卷对话框 -->
    <el-dialog title="阅卷打分" v-model="gradeDialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="考试名称">{{ gradeRecord.examName }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ gradeRecord.studentName }}</el-descriptions-item>
        <el-descriptions-item label="客观题得分">{{ gradeRecord.objectiveScore }}分</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ gradeRecord.submitTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider>主观题阅卷</el-divider>
      
      <div v-for="(answer, index) in subjectiveAnswers" :key="answer.id" class="grade-item">
        <div class="grade-title">
          <span class="grade-number">{{ index + 1 }}.</span>
          <span>{{ answer.title }}</span>
          <span class="grade-type">[{{ answer.typeName }}]</span>
          <span class="grade-score">({{ answer.score }}分)</span>
        </div>
        <div class="grade-content">
          <p><strong>学生答案：</strong></p>
          <div class="student-answer">{{ answer.studentAnswer || '未作答' }}</div>
          <p><strong>参考答案：</strong>{{ answer.correctAnswer }}</p>
        </div>
        <div class="grade-input">
          <el-form-item label="得分">
            <el-input-number v-model="answer.getScore" :min="0" :max="answer.score" />
            <span style="margin-left: 10px">分</span>
          </el-form-item>
          <el-form-item label="评语">
            <el-input v-model="answer.comment" type="textarea" :rows="2" placeholder="请输入评语" />
          </el-form-item>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="gradeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveGrade" :loading="loading">提交阅卷</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看详情对话框 -->
    <el-dialog title="试卷详情" v-model="viewDialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="考试名称">{{ viewRecord.examName }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ viewRecord.studentName }}</el-descriptions-item>
        <el-descriptions-item label="客观题得分">{{ viewRecord.objectiveScore }}分</el-descriptions-item>
        <el-descriptions-item label="主观题得分">{{ viewRecord.subjectiveScore }}分</el-descriptions-item>
        <el-descriptions-item label="总分">
          <span :style="{ color: viewRecord.totalScore >= 60 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
            {{ viewRecord.totalScore }}分
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ viewRecord.submitTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider>答题详情</el-divider>
      
      <div v-for="(answer, index) in allAnswers" :key="answer.id" class="answer-item">
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
          <p><strong>学生答案：</strong>{{ answer.studentAnswer || '未作答' }}</p>
          <p><strong>正确答案：</strong>{{ answer.correctAnswer }}</p>
          <p>
            <strong>得分：</strong>
            <span :style="{ color: answer.getScore >= answer.score ? '#67C23A' : '#F56C6C' }">
              {{ answer.getScore }}分
            </span>
          </p>
        </div>
        <div v-if="answer.comment" class="answer-comment">
          <strong>评语：</strong>{{ answer.comment }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import recordApi from '@/api/record'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  examName: '',
  studentName: '',
  gradeStatus: null
})

const gradeDialogVisible = ref(false)
const gradeRecord = ref({})
const subjectiveAnswers = ref([])

const viewDialogVisible = ref(false)
const viewRecord = ref({})
const allAnswers = ref([])

onMounted(() => {
  loadRecordList()
})

const loadRecordList = async () => {
  loading.value = true
  try {
    const res = await recordApi.search(searchForm.value)
    // 教师只能阅卷自己课程的考试
    recordList.value = res.data?.filter(r => r.teacherId === userInfo.value.id) || []
    total.value = recordList.value.length || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadRecordList()
}

const handleReset = () => {
  searchForm.value = {
    examName: '',
    studentName: '',
    gradeStatus: null
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadRecordList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadRecordList()
}

const handleGrade = async (row) => {
  gradeRecord.value = row
  try {
    const res = await recordApi.getAnswers(row.id)
    // 只显示主观题（填空题和简答题）
    subjectiveAnswers.value = res.data?.filter(a => a.typeCode === 'fill' || a.typeCode === 'essay') || []
    // 初始化得分
    subjectiveAnswers.value.forEach(answer => {
      if (!answer.getScore) {
        answer.getScore = 0
      }
      if (!answer.comment) {
        answer.comment = ''
      }
    })
    gradeDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取答题详情失败')
  }
}

const handleView = async (row) => {
  viewRecord.value = row
  try {
    const res = await recordApi.getAnswers(row.id)
    allAnswers.value = res.data || []
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取答题详情失败')
  }
}

const handleSaveGrade = async () => {
  loading.value = true
  try {
    const gradeData = {
      recordId: gradeRecord.value.id,
      answers: subjectiveAnswers.value.map(a => ({
        id: a.id,
        getScore: a.getScore,
        comment: a.comment
      }))
    }
    await recordApi.gradeExam(gradeData)
    ElMessage.success('阅卷成功')
    gradeDialogVisible.value = false
    loadRecordList()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.teacher-grade {
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

.grade-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}

.grade-title {
  margin-bottom: 10px;
  font-size: 15px;
}

.grade-number {
  font-weight: bold;
  margin-right: 5px;
}

.grade-type {
  color: #409EFF;
  margin-left: 10px;
}

.grade-score {
  color: #E6A23C;
  margin-left: 10px;
}

.grade-content {
  margin-bottom: 15px;
}

.grade-content p {
  margin: 8px 0;
}

.student-answer {
  padding: 10px;
  background: #F5F7FA;
  border-radius: 4px;
  margin: 10px 0;
}

.grade-input {
  margin-top: 15px;
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

.answer-comment {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
</style>