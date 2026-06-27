<template>
  <div class="admin-home">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.creatorCount }}</div>
              <div class="stat-label">出题人总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.candidateCount }}</div>
              <div class="stat-label">考生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.examCount }}</div>
              <div class="stat-label">考试总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 考试安排 -->
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
            <el-table-column prop="paperName" label="试卷名称" />
            <el-table-column prop="startTime" label="开始时间" width="180" />
            <el-table-column prop="endTime" label="结束时间" width="180" />
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import examApi from '@/api/exam'
import creatorApi from '@/api/creator'
import candidateApi from '@/api/candidate'

const router = useRouter()

const stats = ref({
  creatorCount: 0,
  candidateCount: 0,
  examCount: 0
})

const exams = ref([])

onMounted(async () => {
  // 获取统计数据
  try {
    const creators = await creatorApi.getList()
    stats.value.creatorCount = creators.data?.length || 0
    
    const candidates = await candidateApi.getList()
    stats.value.candidateCount = candidates.data?.length || 0
    
    const examList = await examApi.getList()
    stats.value.examCount = examList.data?.length || 0
    exams.value = examList.data?.slice(0, 5) || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
})

const goExam = () => {
  router.push('/admin/exam')
}
</script>

<style scoped>
.admin-home {
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