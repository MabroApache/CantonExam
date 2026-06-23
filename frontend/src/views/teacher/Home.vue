<template>
  <div class="teacher-home">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon><EditPen /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.questionCount }}</div>
              <div class="stat-label">我的题库</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.paperCount }}</div>
              <div class="stat-label">我的试卷</div>
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
              <div class="stat-label">我的考试</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item v-for="notice in notices" :key="notice.id" :timestamp="notice.publishTime" placement="top">
              <el-card>
                <h4>{{ notice.title }}</h4>
                <p>{{ notice.content }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考试安排</span>
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
import { useUserStore } from '@/stores/user'
import noticeApi from '@/api/notice'
import examApi from '@/api/exam'
import questionApi from '@/api/question'
import paperApi from '@/api/paper'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const stats = ref({
  questionCount: 0,
  paperCount: 0,
  examCount: 0
})

const notices = ref([])
const exams = ref([])

onMounted(async () => {
  try {
    const questions = await questionApi.getByTeacherId(userInfo.value.id)
    stats.value.questionCount = questions.data?.length || 0
    
    const papers = await paperApi.getByTeacherId(userInfo.value.id)
    stats.value.paperCount = papers.data?.length || 0
    
    const examList = await examApi.search({ teacherId: userInfo.value.id })
    stats.value.examCount = examList.data?.length || 0
    exams.value = examList.data?.slice(0, 5) || []
    
    const noticeList = await noticeApi.getPublished()
    notices.value = noticeList.data?.slice(0, 3) || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
})
</script>

<style scoped>
.teacher-home {
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