<template>
  <div class="creator-paper">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>试卷管理</span>
          <el-button type="primary" @click="handleManualCreate">手动组卷</el-button>
          <el-button type="success" @click="handleAutoCreate">自动组卷</el-button>
        </div>
      </template>

      <el-table :data="paperList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="试卷名称" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="singleCount" label="单选" width="60" />
        <el-table-column prop="multiCount" label="多选" width="60" />
        <el-table-column prop="judgeCount" label="判断" width="60" />
        <el-table-column prop="fillCount" label="填空" width="60" />
        <el-table-column prop="essayCount" label="简答" width="60" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'info' : 'success'">
              {{ row.status === 0 ? '草稿' : '已发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" style="color: #F56C6C">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 手动组卷对话框 -->
    <el-dialog title="手动组卷" v-model="manualDialogVisible" width="900px">
      <el-form :model="manualForm" :rules="manualRules" ref="manualFormRef" label-width="100px">
        <el-form-item label="试卷名称" prop="name">
          <el-input v-model="manualForm.name" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="manualForm.duration" :min="30" :max="180" />
        </el-form-item>
        <el-form-item label="试卷说明">
          <el-input v-model="manualForm.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
        </el-form-item>

        <el-form-item label="标签筛选">
          <el-select v-model="manualForm.filterTags" multiple placeholder="请输入或选择标签" filterable allow-create style="width: 100%">
            <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>

        <el-form-item label="选择题目">
          <el-tabs v-model="questionTab">
            <el-tab-pane label="单选题" name="single">
              <el-table :data="filteredSingleQuestions" style="width: 100%" max-height="400">
                <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
                <el-table-column prop="tags" label="标签" width="120">
                  <template #default="{ row }">
                    <el-tag v-for="tag in (row.tags || [])" :key="tag" size="small" style="margin-right: 3px">{{ tag }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="分值" width="120">
                  <template #default="{ row }">
                    <el-input-number :model-value="row.customScore" @change="(val) => handleScoreChange(row.id, val)" :min="1" :max="500" :precision="0" style="width: 100%" controls-position="right" />
                  </template>
                </el-table-column>
                <el-table-column label="选择" width="60">
                  <template #default="{ row }">
                    <el-checkbox v-model="manualForm.selectedQuestions" :label="row.id" />
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="多选题" name="multi">
              <el-table :data="filteredMultiQuestions" style="width: 100%" max-height="400">
                <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
                <el-table-column prop="tags" label="标签" width="120">
                  <template #default="{ row }">
                    <el-tag v-for="tag in (row.tags || [])" :key="tag" size="small" style="margin-right: 3px">{{ tag }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="分值" width="120">
                  <template #default="{ row }">
                    <el-input-number :model-value="row.customScore" @change="(val) => handleScoreChange(row.id, val)" :min="1" :max="500" :precision="0" style="width: 100%" controls-position="right" />
                  </template>
                </el-table-column>
                <el-table-column label="选择" width="60">
                  <template #default="{ row }">
                    <el-checkbox v-model="manualForm.selectedQuestions" :label="row.id" />
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="判断题" name="judge">
              <el-table :data="filteredJudgeQuestions" style="width: 100%" max-height="400">
                <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
                <el-table-column prop="tags" label="标签" width="120">
                  <template #default="{ row }">
                    <el-tag v-for="tag in (row.tags || [])" :key="tag" size="small" style="margin-right: 3px">{{ tag }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="分值" width="120">
                  <template #default="{ row }">
                    <el-input-number :model-value="row.customScore" @change="(val) => handleScoreChange(row.id, val)" :min="1" :max="500" :precision="0" style="width: 100%" controls-position="right" />
                  </template>
                </el-table-column>
                <el-table-column label="选择" width="60">
                  <template #default="{ row }">
                    <el-checkbox v-model="manualForm.selectedQuestions" :label="row.id" />
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="填空题" name="fill">
              <el-table :data="filteredFillQuestions" style="width: 100%" max-height="400">
                <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
                <el-table-column prop="tags" label="标签" width="120">
                  <template #default="{ row }">
                    <el-tag v-for="tag in (row.tags || [])" :key="tag" size="small" style="margin-right: 3px">{{ tag }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="分值" width="120">
                  <template #default="{ row }">
                    <el-input-number :model-value="row.customScore" @change="(val) => handleScoreChange(row.id, val)" :min="1" :max="500" :precision="0" style="width: 100%" controls-position="right" />
                  </template>
                </el-table-column>
                <el-table-column label="选择" width="60">
                  <template #default="{ row }">
                    <el-checkbox v-model="manualForm.selectedQuestions" :label="row.id" />
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="简答题" name="essay">
              <el-table :data="filteredEssayQuestions" style="width: 100%" max-height="400">
                <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
                <el-table-column prop="tags" label="标签" width="120">
                  <template #default="{ row }">
                    <el-tag v-for="tag in (row.tags || [])" :key="tag" size="small" style="margin-right: 3px">{{ tag }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="分值" width="120">
                  <template #default="{ row }">
                    <el-input-number :model-value="row.customScore" @change="(val) => handleScoreChange(row.id, val)" :min="1" :max="500" :precision="0" style="width: 100%" controls-position="right" />
                  </template>
                </el-table-column>
                <el-table-column label="选择" width="60">
                  <template #default="{ row }">
                    <el-checkbox v-model="manualForm.selectedQuestions" :label="row.id" />
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-form-item>

        <el-form-item label="试卷总分" prop="totalScore">
          <el-input-number v-model="manualForm.totalScore" :min="1" :max="500" style="width: 200px" />
        </el-form-item>
        
        <el-form-item label="题目合计分数">
          <span :style="{ fontSize: '18px', fontWeight: 'bold', color: manualScoreMatch ? '#67C23A' : '#F56C6C' }">
            {{ manualCalculatedScore }}分
          </span>
          <span v-if="!manualScoreMatch" style="margin-left: 10px; color: #F56C6C">
            与试卷总分不一致
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="manualDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleManualSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 自动组卷对话框 -->
    <el-dialog title="自动组卷" v-model="autoDialogVisible" width="600px">
      <el-form :model="autoForm" :rules="autoRules" ref="autoFormRef" label-width="100px">
        <el-form-item label="试卷名称" prop="name">
          <el-input v-model="autoForm.name" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="autoForm.duration" :min="30" :max="180" />
        </el-form-item>
        <el-form-item label="试卷说明">
          <el-input v-model="autoForm.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
        </el-form-item>

        <el-divider>题目配置</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单选题数量">
              <el-input-number v-model="autoForm.singleCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单选题总分">
              <el-input-number v-model="autoForm.singleTotalScore" :min="0" :max="100" />
              <span style="margin-left: 10px; color: #909399">每题 {{ autoSingleAvgScore }}分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="多选题数量">
              <el-input-number v-model="autoForm.multiCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="多选题总分">
              <el-input-number v-model="autoForm.multiTotalScore" :min="0" :max="100" />
              <span style="margin-left: 10px; color: #909399">每题 {{ autoMultiAvgScore }}分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="判断题数量">
              <el-input-number v-model="autoForm.judgeCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="判断题总分">
              <el-input-number v-model="autoForm.judgeTotalScore" :min="0" :max="100" />
              <span style="margin-left: 10px; color: #909399">每题 {{ autoJudgeAvgScore }}分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="填空题数量">
              <el-input-number v-model="autoForm.fillCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="填空题总分">
              <el-input-number v-model="autoForm.fillTotalScore" :min="0" :max="100" />
              <span style="margin-left: 10px; color: #909399">每题 {{ autoFillAvgScore }}分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="简答题数量">
              <el-input-number v-model="autoForm.essayCount" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="简答题总分">
              <el-input-number v-model="autoForm.essayTotalScore" :min="0" :max="100" />
              <span style="margin-left: 10px; color: #909399">每题 {{ autoEssayAvgScore }}分</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="试卷总分">
          <span style="font-size: 18px; font-weight: bold; color: #E6A23C">{{ autoTotalScore }}分</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="autoDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAutoSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看试卷详情 -->
    <el-dialog title="试卷详情" v-model="viewDialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="试卷名称">{{ viewPaper.name }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ viewPaper.totalScore }}分</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ viewPaper.duration }}分钟</el-descriptions-item>
        <el-descriptions-item label="试卷说明" :span="2">{{ viewPaper.description || '无' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>题目列表</el-divider>

      <div v-for="(question, index) in paperQuestions" :key="question.id" class="question-item">
        <div class="question-title">
          <span class="question-number">{{ index + 1 }}.</span>
          <span>{{ question.title }}</span>
          <span class="question-type">[{{ question.typeName }}]</span>
          <span class="question-score">({{ question.score }}分)</span>
        </div>
        <div v-if="question.optionA" class="question-options">
          <div>A. {{ question.optionA }}</div>
          <div>B. {{ question.optionB }}</div>
          <div>C. {{ question.optionC }}</div>
          <div>D. {{ question.optionD }}</div>
        </div>
        <div class="question-answer">
          <strong>答案：</strong>{{ question.answer }}
        </div>
        <div v-if="question.analysis" class="question-analysis">
          <strong>解析：</strong>{{ question.analysis }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import paperApi from '@/api/paper'
import questionApi from '@/api/question'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const paperList = ref([])
const allQuestions = ref([])
const allTags = ref([])
const questionScores = ref({})

const questionTab = ref('single')

const manualDialogVisible = ref(false)
const manualForm = ref({
  name: '',
  creatorId: userInfo.value.id,
  creatorName: userInfo.value.name,
  departmentId: userInfo.value.departmentId,
  duration: 60,
  description: '',
  filterTags: [],
  selectedQuestions: [],
  totalScore: 100
})

const manualRules = {
  name: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

const manualFormRef = ref(null)

const autoDialogVisible = ref(false)
const autoForm = ref({
  name: '',
  creatorId: userInfo.value.id,
  creatorName: userInfo.value.name,
  departmentId: userInfo.value.departmentId,
  duration: 60,
  description: '',
  singleCount: 10,
  singleTotalScore: 50,
  multiCount: 5,
  multiTotalScore: 50,
  judgeCount: 10,
  judgeTotalScore: 50,
  fillCount: 5,
  fillTotalScore: 50,
  essayCount: 2,
  essayTotalScore: 40
})

const autoRules = {
  name: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

const autoFormRef = ref(null)

const viewDialogVisible = ref(false)
const viewPaper = ref({})
const paperQuestions = ref([])

const singleQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '单选题'))
const multiQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '多选题'))
const judgeQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '判断题'))
const fillQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '填空题'))
const essayQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '简答题'))

const filteredSingleQuestions = computed(() => {
  if (!manualForm.value.filterTags || manualForm.value.filterTags.length === 0) {
    return singleQuestions.value.map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
  }
  return singleQuestions.value.filter(q => {
    if (!q.tags || !Array.isArray(q.tags)) return false
    return manualForm.value.filterTags.some(tag => q.tags.includes(tag))
  }).map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
})

const filteredMultiQuestions = computed(() => {
  if (!manualForm.value.filterTags || manualForm.value.filterTags.length === 0) {
    return multiQuestions.value.map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
  }
  return multiQuestions.value.filter(q => {
    if (!q.tags || !Array.isArray(q.tags)) return false
    return manualForm.value.filterTags.some(tag => q.tags.includes(tag))
  }).map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
})

const filteredJudgeQuestions = computed(() => {
  if (!manualForm.value.filterTags || manualForm.value.filterTags.length === 0) {
    return judgeQuestions.value.map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
  }
  return judgeQuestions.value.filter(q => {
    if (!q.tags || !Array.isArray(q.tags)) return false
    return manualForm.value.filterTags.some(tag => q.tags.includes(tag))
  }).map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
})

const filteredFillQuestions = computed(() => {
  if (!manualForm.value.filterTags || manualForm.value.filterTags.length === 0) {
    return fillQuestions.value.map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
  }
  return fillQuestions.value.filter(q => {
    if (!q.tags || !Array.isArray(q.tags)) return false
    return manualForm.value.filterTags.some(tag => q.tags.includes(tag))
  }).map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 5) }))
})

const filteredEssayQuestions = computed(() => {
  if (!manualForm.value.filterTags || manualForm.value.filterTags.length === 0) {
    return essayQuestions.value.map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 10) }))
  }
  return essayQuestions.value.filter(q => {
    if (!q.tags || !Array.isArray(q.tags)) return false
    return manualForm.value.filterTags.some(tag => q.tags.includes(tag))
  }).map(q => ({ ...q, customScore: questionScores.value[q.id] !== undefined ? questionScores.value[q.id] : (q.score || 10) }))
})

const manualTotalScore = computed(() => {
  let total = 0
  const allFiltered = [
    ...filteredSingleQuestions.value,
    ...filteredMultiQuestions.value,
    ...filteredJudgeQuestions.value,
    ...filteredFillQuestions.value,
    ...filteredEssayQuestions.value
  ]
  allFiltered.forEach(q => {
    if (manualForm.value.selectedQuestions.includes(q.id)) {
      total += q.customScore || 0
    }
  })
  return total
})

const manualCalculatedScore = computed(() => {
  let total = 0
  const allFiltered = [
    ...filteredSingleQuestions.value,
    ...filteredMultiQuestions.value,
    ...filteredJudgeQuestions.value,
    ...filteredFillQuestions.value,
    ...filteredEssayQuestions.value
  ]
  allFiltered.forEach(q => {
    if (manualForm.value.selectedQuestions.includes(q.id)) {
      total += q.customScore || 0
    }
  })
  return total
})

const manualScoreMatch = computed(() => {
  return manualCalculatedScore.value === manualForm.value.totalScore
})

const autoSingleAvgScore = computed(() => {
  if (autoForm.value.singleCount === 0) return 0
  return Math.round(autoForm.value.singleTotalScore / autoForm.value.singleCount)
})

const autoMultiAvgScore = computed(() => {
  if (autoForm.value.multiCount === 0) return 0
  return Math.round(autoForm.value.multiTotalScore / autoForm.value.multiCount)
})

const autoJudgeAvgScore = computed(() => {
  if (autoForm.value.judgeCount === 0) return 0
  return Math.round(autoForm.value.judgeTotalScore / autoForm.value.judgeCount)
})

const autoFillAvgScore = computed(() => {
  if (autoForm.value.fillCount === 0) return 0
  return Math.round(autoForm.value.fillTotalScore / autoForm.value.fillCount)
})

const autoEssayAvgScore = computed(() => {
  if (autoForm.value.essayCount === 0) return 0
  return Math.round(autoForm.value.essayTotalScore / autoForm.value.essayCount)
})

const autoTotalScore = computed(() => {
  return autoForm.value.singleTotalScore +
    autoForm.value.multiTotalScore +
    autoForm.value.judgeTotalScore +
    autoForm.value.fillTotalScore +
    autoForm.value.essayTotalScore
})

onMounted(async () => {
  await loadPaperList()
})

const loadPaperList = async () => {
  loading.value = true
  try {
    const res = await paperApi.getList()
    paperList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const loadQuestions = async () => {
  try {
    const res = await questionApi.getList()
    allQuestions.value = res.data || []
    // 提取所有标签
    const tagsSet = new Set()
    allQuestions.value.forEach(q => {
      if (q.tags && Array.isArray(q.tags)) {
        q.tags.forEach(tag => tagsSet.add(tag))
      }
    })
    allTags.value = Array.from(tagsSet)
  } catch (error) {
    console.error('获取题目失败', error)
  }
}

const handleScoreChange = (questionId, value) => {
  questionScores.value[questionId] = value
}

const handleManualCreate = async () => {
  manualForm.value = {
    name: '',
    creatorId: userInfo.value.id,
    creatorName: userInfo.value.name,
    departmentId: userInfo.value.departmentId,
    duration: 60,
    description: '',
    filterTags: [],
    selectedQuestions: [],
    totalScore: 100
  }
  questionScores.value = {}
  await loadQuestions()
  manualDialogVisible.value = true
}

const handleAutoCreate = async () => {
  autoForm.value = {
    name: '',
    creatorId: userInfo.value.id,
    creatorName: userInfo.value.name,
    departmentId: userInfo.value.departmentId,
    duration: 60,
    description: '',
    singleCount: 10,
    singleTotalScore: 50,
    multiCount: 5,
    multiTotalScore: 50,
    judgeCount: 10,
    judgeTotalScore: 50,
    fillCount: 5,
    fillTotalScore: 50,
    essayCount: 2,
    essayTotalScore: 40
  }
  autoDialogVisible.value = true
}

const handleManualSave = async () => {
  await manualFormRef.value.validate()
  if (manualForm.value.selectedQuestions.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }
  
  if (!manualScoreMatch.value) {
    ElMessage.warning(`题目合计分数(${manualCalculatedScore.value}分)与试卷总分(${manualForm.value.totalScore}分)不一致，请调整分值`)
    return
  }

  loading.value = true
  try {
    const questionIds = manualForm.value.selectedQuestions
    const scores = questionIds.map(qId => questionScores.value[qId] !== undefined ? questionScores.value[qId] : 5)

    const formData = {
      name: manualForm.value.name,
      creatorId: manualForm.value.creatorId,
      creatorName: manualForm.value.creatorName,
      departmentId: manualForm.value.departmentId,
      duration: manualForm.value.duration,
      description: manualForm.value.description,
      questionIds,
      scores,
      totalScore: manualForm.value.totalScore
    }
    await paperApi.manualCreate(formData)
    ElMessage.success('组卷成功')
    manualDialogVisible.value = false
    loadPaperList()
  } catch (error) {
    console.error('组卷失败', error)
    ElMessage.error(error.response?.data?.msg || '组卷失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleAutoSave = async () => {
  await autoFormRef.value.validate()

  loading.value = true
  try {
    const formData = {
      ...autoForm.value,
      singleScore: autoSingleAvgScore.value,
      multiScore: autoMultiAvgScore.value,
      judgeScore: autoJudgeAvgScore.value,
      fillScore: autoFillAvgScore.value,
      essayScore: autoEssayAvgScore.value,
      totalScore: autoTotalScore.value
    }
    await paperApi.autoCreate(formData)
    ElMessage.success('组卷成功')
    autoDialogVisible.value = false
    loadPaperList()
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  viewPaper.value = row
  try {
    const res = await paperApi.getQuestions(row.id)
    paperQuestions.value = res.data || []
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取试卷题目失败')
  }
}

const handleEdit = (row) => {
  ElMessage.info('编辑试卷功能待实现')
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该试卷吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await paperApi.delete(row.id)
    ElMessage.success('删除成功')
    loadPaperList()
  }).catch(() => {})
}
</script>

<style scoped>
.creator-paper {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
}

.question-title {
  margin-bottom: 10px;
  font-size: 15px;
}

.question-number {
  font-weight: bold;
  margin-right: 5px;
}

.question-type {
  color: #409EFF;
  margin-left: 10px;
}

.question-score {
  color: #E6A23C;
  margin-left: 10px;
}

.question-options {
  margin-left: 20px;
  margin-bottom: 10px;
  color: #606266;
}

.question-options div {
  margin: 5px 0;
}

.question-answer {
  margin-top: 10px;
  color: #67C23A;
}

.question-analysis {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
</style>