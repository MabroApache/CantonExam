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
              <div class="stat-value">{{ stats.teacherCount }}</div>
              <div class="stat-label">教师总数</div>
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
              <div class="stat-value">{{ stats.studentCount }}</div>
              <div class="stat-label">学生总数</div>
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
      <!-- 系统公告 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
              <el-button type="text" @click="goNotice">查看更多</el-button>
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
      
      <!-- 考试安排 -->
      <el-col :span="12">
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import noticeApi from '@/api/notice'
import examApi from '@/api/exam'
import teacherApi from '@/api/teacher'
import studentApi from '@/api/student'

const router = useRouter()

const stats = ref({
  teacherCount: 0,
  studentCount: 0,
  examCount: 0
})

const notices = ref([])
const exams = ref([])

onMounted(async () => {
  // 获取统计数据
  try {
    const teachers = await teacherApi.getList()
    stats.value.teacherCount = teachers.data?.length || 0
    
    const students = await studentApi.getList()
    stats.value.studentCount = students.data?.length || 0
    
    const examList = await examApi.getList()
    stats.value.examCount = examList.data?.length || 0
    exams.value = examList.data?.slice(0, 5) || []
    
    const noticeList = await noticeApi.getPublished()
    notices.value = noticeList.data?.slice(0, 3) || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
})

const goNotice = () => {
  router.push('/admin/notice')
}

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