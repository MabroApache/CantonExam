<template>
  <div class="candidate-exam">
    <el-card>
      <template #header>
        <span>在线考试</span>
      </template>

      <!-- 考试列表 -->
      <el-table :data="examList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="考试名称" />
        <el-table-column prop="creatorName" label="创建人" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">
              {{ getStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEnterExam(row)"
                       v-if="getCurrentStatus(row) === 1 && !hasExamRecord(row.id)">进入考试</el-button>
            <el-tag v-if="hasExamRecord(row.id)" type="success">已参加</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import examApi from '@/api/exam'
import recordApi from '@/api/record'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const examList = ref([])
const examRecords = ref([])

onMounted(async () => {
  loadExamList()
  loadExamRecords()
})

const loadExamList = async () => {
  loading.value = true
  try {
    const res = await examApi.getByCandidateId(userInfo.value.id)
    examList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const loadExamRecords = async () => {
  try {
    const res = await recordApi.search({ candidateId: userInfo.value.id })
    examRecords.value = res.data || []
  } catch (error) {
    console.error('获取考试记录失败', error)
  }
}

const hasExamRecord = (examId) => {
  return examRecords.value.some(record => record.examId === examId)
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

const handleEnterExam = (exam) => {
  ElMessageBox.confirm(`确定要参加考试"${exam.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push(`/candidate/exam/${exam.id}`)
  }).catch(() => {})
}
</script>

<style scoped>
.candidate-exam {
  padding: 20px;
}
</style>