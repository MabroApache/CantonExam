<template>
  <div class="student-forum">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>交流论坛</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8" v-for="share in shareList" :key="share.id">
          <el-card shadow="hover" class="share-card" @click="handleViewShare(share)">
            <div class="share-title">{{ share.title }}</div>
            <div class="share-author">
              <el-avatar :size="30" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"></el-avatar>
              <span>{{ share.studentName }}</span>
            </div>
            <div class="share-content">{{ share.content }}</div>
            <div class="share-stats">
              <span><el-icon><View /></el-icon> {{ share.viewCount }}</span>
              <span><el-icon><Star /></el-icon> {{ share.likeCount }}</span>
              <span><el-icon><Clock /></el-icon> {{ share.createTime }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 查看分享详情对话框 -->
    <el-dialog title="分享详情" v-model="shareDialogVisible" width="600px">
      <div v-if="currentShare">
        <h2>{{ currentShare.title }}</h2>
        <div class="share-detail-author">
          <el-avatar :size="40" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"></el-avatar>
          <span>{{ currentShare.studentName }}</span>
          <span>{{ currentShare.createTime }}</span>
        </div>
        <div class="share-detail-content">
          {{ currentShare.content }}
        </div>
        <div class="share-detail-stats">
          <el-button type="text" @click="handleLike(currentShare.id)">
            <el-icon><Star /></el-icon> 点赞 {{ currentShare.likeCount }}
          </el-button>
          <span><el-icon><View /></el-icon> 浏览 {{ currentShare.viewCount }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import shareApi from '@/api/share'

const loading = ref(false)
const shareList = ref([])

const shareDialogVisible = ref(false)
const currentShare = ref(null)

onMounted(() => {
  loadShareList()
})

const loadShareList = async () => {
  loading.value = true
  try {
    const res = await shareApi.getList()
    shareList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleViewShare = async (share) => {
  currentShare.value = share
  shareDialogVisible.value = true
  
  // 增加浏览次数
  try {
    await shareApi.view(share.id)
    share.viewCount++
  } catch (error) {
    console.error('增加浏览次数失败', error)
  }
}

const handleLike = async (shareId) => {
  try {
    await shareApi.like(shareId)
    currentShare.value.likeCount++
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞失败', error)
  }
}
</script>

<style scoped>
.student-forum {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.share-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.share-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.share-author {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.share-author span {
  margin-left: 10px;
  color: #999;
}

.share-content {
  color: #666;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
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

.share-detail-author {
  display: flex;
  align-items: center;
  margin: 20px 0;
}

.share-detail-author span {
  margin-left: 10px;
}

.share-detail-content {
  margin: 20px 0;
  line-height: 1.8;
}

.share-detail-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}
</style>