<template>
  <div class="student-score">
    <el-card>
      <template #header>
        <span>我的成绩</span>
      </template>
      
      <el-table :data="scoreList" style="width: 100%" v-loading="loading">
        <el-table-column prop="examName" label="考试名称" />
        <el-table-column prop="totalScore" label="总分" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.totalScore >= 60 ? '#67C23A' : '#F56C6C' }">
              {{ row.totalScore }}分
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="objectiveScore" label="客观题得分" width="120" />
        <el-table-column prop="subjectiveScore" label="主观题得分" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleViewPaper(row)">查看试卷</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 查看试卷对话框 -->
    <el-dialog title="试卷详情" v-model="paperDialogVisible" width="800px">
      <div v-if="currentScore">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="考试名称">{{ currentScore.examName }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ currentScore.totalScore }}分</el-descriptions-item>
          <el-descriptions-item label="客观题得分">{{ currentScore.objectiveScore }}分</el-descriptions-item>
          <el-descriptions-item label="主观题得分">{{ currentScore.subjectiveScore }}分</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentScore.submitTime }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider>答题详情</el-divider>
        
        <div v-for="(answer, index) in answerList" :key="answer.id" class="answer-item">
          <div class="answer-title">
            <span class="answer-number">{{ index + 1 }}.</span>
            <span>{{ answer.typeName }}</span>
            <span class="answer-score">（{{ answer.score }}分）</span>
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
            <p v-if="answer.comment"><strong>批注：</strong>{{ answer.comment }}</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import scoreApi from '@/api/score'
import recordApi from '@/api/record'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const scoreList = ref([])

const paperDialogVisible = ref(false)
const currentScore = ref(null)
const answerList = ref([])

onMounted(() => {
  loadScoreList()
})

const loadScoreList = async () => {
  loading.value = true
  try {
    const res = await scoreApi.getByStudentId(userInfo.value.id)
    scoreList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleViewPaper = async (row) => {
  currentScore.value = row
  paperDialogVisible.value = true
  
  // 获取答题详情
  try {
    const recordRes = await recordApi.search({ 
      examId: row.examId, 
      studentId: userInfo.value.id 
    })
    const record = recordRes.data?.[0]
    if (record) {
      const answerRes = await recordApi.getAnswers(record.id)
      answerList.value = answerRes.data || []
    }
  } catch (error) {
    console.error('获取答题详情失败', error)
  }
}
</script>

<style scoped>
.student-score {
  padding: 20px;
}

.answer-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 5px;
}

.answer-title {
  font-size: 16px;
  margin-bottom: 10px;
}

.answer-number {
  font-weight: bold;
  margin-right: 10px;
}

.answer-score {
  color: #F56C6C;
  margin-left: 10px;
}

.answer-content p {
  margin: 5px 0;
}
</style>