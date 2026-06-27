<template>
  <div class="candidate-home">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.examCount }}</div>
              <div class="stat-label">可参加考试</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.scoreCount }}</div>
              <div class="stat-label">我的成绩</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考试安排</span>
              <el-button type="text" @click="goExam">查看更多</el-button>
            </div>
          </template>
          <el-table :data="exams" style="width: 100%">
            <el-table-column prop="name" label="考试名称" />
            <el-table-column prop="startTime" label="开始时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 0 ? 'info' : row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 0 ? '未开始' : row.status === 1 ? '进行中' : '已结束' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import examApi from '@/api/exam'
import scoreApi from '@/api/score'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const stats = ref({
  examCount: 0,
  scoreCount: 0
})

const exams = ref([])

onMounted(async () => {
  try {
    // 获取所有考试列表
    const examList = await examApi.getList()
    stats.value.examCount = examList.data?.length || 0
    exams.value = examList.data?.slice(0, 5) || []

    // 获取成绩列表
    const scores = await scoreApi.getByCandidateId(userInfo.value.id)
    stats.value.scoreCount = scores.data?.length || 0
  } catch (error) {
    console.error('获取数据失败', error)
  }
})

const goExam = () => {
  router.push('/candidate/exam')
}
</script>

<style scoped>
.candidate-home {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 30px;
}

.stat-info {
  margin-left: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>