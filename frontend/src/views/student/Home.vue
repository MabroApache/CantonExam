<template>
  <div class="student-home">
    <el-row :gutter="20">
      <el-col :span="6">
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
      <el-col :span="6">
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
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon><EditPen /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.shareCount }}</div>
              <div class="stat-label">我的分享</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.forumCount }}</div>
              <div class="stat-label">论坛帖子</div>
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
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门分享</span>
              <el-button type="text" @click="goForum">查看更多</el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="8" v-for="share in shares" :key="share.id">
              <el-card shadow="hover" class="share-card">
                <div class="share-title">{{ share.title }}</div>
                <div class="share-author">{{ share.studentName }}</div>
                <div class="share-stats">
                  <span><el-icon><View /></el-icon> {{ share.viewCount }}</span>
                  <span><el-icon><Star /></el-icon> {{ share.likeCount }}</span>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import noticeApi from '@/api/notice'
import examApi from '@/api/exam'
import scoreApi from '@/api/score'


const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const stats = ref({
  examCount: 0,
  scoreCount: 0,
  shareCount: 0,
  forumCount: 0
})

const notices = ref([])
const exams = ref([])
const shares = ref([])

onMounted(async () => {
  try {
    const examList = await examApi.getByStudentId(userInfo.value.id)
    stats.value.examCount = examList.data?.length || 0
    exams.value = examList.data?.slice(0, 5) || []
    
    const scores = await scoreApi.getByStudentId(userInfo.value.id)
    stats.value.scoreCount = scores.data?.length || 0
    
    const myShares = await shareApi.getByStudentId(userInfo.value.id)
    stats.value.shareCount = myShares.data?.length || 0
    
    const allShares = await shareApi.getList()
    stats.value.forumCount = allShares.data?.length || 0
    shares.value = allShares.data?.slice(0, 6) || []
    
    const noticeList = await noticeApi.getPublished()
    notices.value = noticeList.data?.slice(0, 3) || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
})

const goExam = () => {
  router.push('/student/exam')
}

const goForum = () => {
  router.push('/student/forum')
}
</script>

<style scoped>
.student-home {
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

.share-card {
  margin-bottom: 20px;
}

.share-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.share-author {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.share-stats {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

.share-stats span {
  display: flex;
  align-items: center;
}

.share-stats span .el-icon {
  margin-right: 5px;
}
</style>