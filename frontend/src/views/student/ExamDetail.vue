<template>
  <div class="exam-detail">
    <el-card v-if="examInfo">
      <template #header>
        <div class="card-header">
          <span>{{ examInfo.name }}</span>
          <div class="exam-timer">
            <el-tag type="danger" size="large">
              <el-icon><Timer /></el-icon>
              剩余时间：{{ formatTime(remainingTime) }}
            </el-tag>
          </div>
        </div>
      </template>
      
      <div class="exam-info">
        <p><strong>总分：</strong>{{ examInfo.totalScore }}分</p>
        <p><strong>时长：</strong>{{ examInfo.duration }}分钟</p>
      </div>
      
      <div class="question-area">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="单选题" name="single" v-if="singleQuestions.length > 0">
            <div class="question-item" v-for="(question, index) in singleQuestions" :key="question.id">
              <div class="question-title">
                <span class="question-number">{{ index + 1 }}.</span>
                <span>{{ question.title }}</span>
                <span class="question-score">（{{ question.score }}分）</span>
              </div>
              <el-radio-group v-model="answers[question.id]" class="question-options">
                <el-radio label="A" v-if="question.optionA">A. {{ question.optionA }}</el-radio>
                <el-radio label="B" v-if="question.optionB">B. {{ question.optionB }}</el-radio>
                <el-radio label="C" v-if="question.optionC">C. {{ question.optionC }}</el-radio>
                <el-radio label="D" v-if="question.optionD">D. {{ question.optionD }}</el-radio>
              </el-radio-group>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="多选题" name="multi" v-if="multiQuestions.length > 0">
            <div class="question-item" v-for="(question, index) in multiQuestions" :key="question.id">
              <div class="question-title">
                <span class="question-number">{{ index + 1 }}.</span>
                <span>{{ question.title }}</span>
                <span class="question-score">（{{ question.score }}分）</span>
              </div>
              <el-checkbox-group v-model="multiAnswers[question.id]" class="question-options">
                <el-checkbox label="A" v-if="question.optionA">A. {{ question.optionA }}</el-checkbox>
                <el-checkbox label="B" v-if="question.optionB">B. {{ question.optionB }}</el-checkbox>
                <el-checkbox label="C" v-if="question.optionC">C. {{ question.optionC }}</el-checkbox>
                <el-checkbox label="D" v-if="question.optionD">D. {{ question.optionD }}</el-checkbox>
              </el-checkbox-group>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="判断题" name="judge" v-if="judgeQuestions.length > 0">
            <div class="question-item" v-for="(question, index) in judgeQuestions" :key="question.id">
              <div class="question-title">
                <span class="question-number">{{ index + 1 }}.</span>
                <span>{{ question.title }}</span>
                <span class="question-score">（{{ question.score }}分）</span>
              </div>
              <el-radio-group v-model="answers[question.id]" class="question-options">
                <el-radio label="正确">正确</el-radio>
                <el-radio label="错误">错误</el-radio>
              </el-radio-group>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="填空题" name="fill" v-if="fillQuestions.length > 0">
            <div class="question-item" v-for="(question, index) in fillQuestions" :key="question.id">
              <div class="question-title">
                <span class="question-number">{{ index + 1 }}.</span>
                <span>{{ question.title }}</span>
                <span class="question-score">（{{ question.score }}分）</span>
              </div>
              <el-input v-model="answers[question.id]" placeholder="请输入答案" />
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="简答题" name="essay" v-if="essayQuestions.length > 0">
            <div class="question-item" v-for="(question, index) in essayQuestions" :key="question.id">
              <div class="question-title">
                <span class="question-number">{{ index + 1 }}.</span>
                <span>{{ question.title }}</span>
                <span class="question-score">（{{ question.score }}分）</span>
              </div>
              <el-input v-model="answers[question.id]" type="textarea" :rows="5" placeholder="请输入答案" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <div class="submit-area">
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交试卷</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import examApi from '@/api/exam'
import paperApi from '@/api/paper'
import recordApi from '@/api/record'
import questionApi from '@/api/question'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const examId = Number(route.params.examId)

if (isNaN(examId) || examId <= 0) {
  console.error('无效的examId:', route.params.examId)
  ElMessage.error('无效的考试ID')
  router.push('/student/exam')
}
const examInfo = ref(null)
const paperQuestions = ref([])
const questions = ref([])
const examRecord = ref(null)

const activeTab = ref('single')
const answers = ref({})
const multiAnswers = ref({})
const submitting = ref(false)

const remainingTime = ref(0)
let timer = null

const singleQuestions = computed(() => questions.value.filter(q => q.type === 1))
const multiQuestions = computed(() => questions.value.filter(q => q.type === 2))
const judgeQuestions = computed(() => questions.value.filter(q => q.type === 3))
const fillQuestions = computed(() => questions.value.filter(q => q.type === 4))
const essayQuestions = computed(() => questions.value.filter(q => q.type === 5))

const getTypeName = (type) => {
  const types = {
    1: '单选题',
    2: '多选题',
    3: '判断题',
    4: '填空题',
    5: '简答题'
  }
  return types[type] || '单选题'
}

onMounted(async () => {
  userStore.setSidebarDisabled(true)
  
  console.log('进入考试页面，examId:', examId)
  
  try {
    await loadExamInfo()
    console.log('考试信息加载成功:', examInfo.value)
    
    await loadExamRecord()
    console.log('考试记录加载成功:', examRecord.value)
    
    startTimer()
    console.log('计时器已启动，剩余时间:', remainingTime.value)
    
  } catch (error) {
    console.error('进入考试失败:', error)
    ElMessage.error('进入考试失败：' + (error.message || error))
    userStore.setSidebarDisabled(false)
    router.push('/student/exam')
  }
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  userStore.setSidebarDisabled(false)
})

const loadExamInfo = async () => {
  console.log('开始加载考试信息，examId:', examId)
  
  const res = await examApi.getById(examId)
  console.log('考试信息API返回:', res)
  
  examInfo.value = res.data
  console.log('考试信息:', examInfo.value)
  
  const paperRes = await paperApi.getQuestions(examInfo.value.paperId)
  console.log('试卷题目API返回:', paperRes)
  
  paperQuestions.value = paperRes.data || []
  console.log('试卷题目列表:', paperQuestions.value)
  
  const questionDetails = await Promise.all(
    paperQuestions.value.map(pq => questionApi.getById(pq.questionId))
  )
  console.log('题目详情:', questionDetails)
  
  questions.value = paperQuestions.value.map((pq, index) => {
    const q = questionDetails[index]?.data || {}
    return {
      id: pq.questionId,
      score: pq.score,
      type: pq.typeId || q.typeId || 1,
      title: q.title || '题目内容',
      optionA: q.optionA,
      optionB: q.optionB,
      optionC: q.optionC,
      optionD: q.optionD
    }
  })
  console.log('构建后的题目列表:', questions.value)
  
  questions.value.forEach(q => {
    if (q.type === 2) {
      multiAnswers.value[q.id] = []
    } else {
      answers.value[q.id] = ''
    }
  })
  
  if (judgeQuestions.value.length > 0) activeTab.value = 'judge'
  else if (singleQuestions.value.length > 0) activeTab.value = 'single'
  else if (multiQuestions.value.length > 0) activeTab.value = 'multi'
  else if (fillQuestions.value.length > 0) activeTab.value = 'fill'
  else if (essayQuestions.value.length > 0) activeTab.value = 'essay'
}

const loadExamRecord = async () => {
  console.log('开始加载考试记录')
  
  const recordId = localStorage.getItem('currentRecordId')
  console.log('localStorage中的recordId:', recordId)
  
  if (recordId) {
    try {
      const res = await recordApi.getById(recordId)
      examRecord.value = res.data
      console.log('已存在的考试记录:', examRecord.value)
    } catch (error) {
      console.log('已存在的记录不存在，准备创建新记录:', error)
      examRecord.value = null
    }
  }
  
  if (!examRecord.value) {
    console.log('创建新的考试记录，参数:', {
      examId: examId,
      paperId: examInfo.value.paperId,
      studentId: userInfo.value.id,
      studentName: userInfo.value.name
    })
    
    const res = await recordApi.startExam({
      examId: examId,
      paperId: examInfo.value.paperId,
      studentId: userInfo.value.id,
      studentName: userInfo.value.name
    })
    console.log('新考试记录创建成功:', res)
    
    examRecord.value = res.data
    localStorage.setItem('currentRecordId', res.data.id)
  }
  
  const startTime = new Date(examRecord.value.startTime)
  const now = new Date()
  const elapsedSeconds = Math.floor((now - startTime) / 1000)
  const totalSeconds = examInfo.value.duration * 60
  
  if (elapsedSeconds >= totalSeconds) {
    remainingTime.value = 0
  } else {
    remainingTime.value = totalSeconds - elapsedSeconds
  }
  
  console.log('剩余时间:', remainingTime.value)
}

const startTimer = () => {
  timer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      clearInterval(timer)
      ElMessage.warning('考试时间已到，系统将自动提交试卷')
      handleSubmit()
    }
  }, 1000)
}

const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const handleSubmit = () => {
  ElMessageBox.confirm('确定要提交试卷吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    submitting.value = true
    try {
      const answerList = questions.value.map(q => {
        let answer = ''
        if (q.type === 2) {
          answer = multiAnswers.value[q.id]?.sort().join('') || ''
        } else {
          answer = answers.value[q.id] || ''
        }
        
        return {
          questionId: q.id,
          studentAnswer: answer,
          score: q.score
        }
      })
      
      const recordId = localStorage.getItem('currentRecordId')
      await recordApi.submitExam({
        recordId: recordId,
        answers: answerList
      })
      
      ElMessage.success('试卷提交成功')
      localStorage.removeItem('currentRecordId')
      router.push('/student/score')
    } catch (error) {
      ElMessage.error('提交失败：' + (error.message || '系统异常'))
    } finally {
      submitting.value = false
    }
  }).catch(() => {})
}
</script>

<style scoped>
.exam-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-timer {
  font-size: 18px;
}

.exam-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 5px;
}

.exam-info p {
  margin: 5px 0;
}

.question-area {
  margin-top: 20px;
}

.question-item {
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 5px;
}

.question-title {
  font-size: 16px;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  margin-right: 10px;
}

.question-score {
  color: #F56C6C;
  margin-left: 10px;
}

.question-options {
  margin-top: 10px;
}

.question-options .el-radio,
.question-options .el-checkbox {
  margin: 10px 0;
  display: block;
}

.submit-area {
  margin-top: 30px;
  text-align: center;
}
</style>