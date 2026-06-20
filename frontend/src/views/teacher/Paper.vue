<template>
  <div class="teacher-paper">
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
        <el-table-column prop="courseName" label="课程" width="150" />
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
    <el-dialog title="手动组卷" v-model="manualDialogVisible" width="800px">
      <el-form :model="manualForm" :rules="manualRules" ref="manualFormRef" label-width="100px">
        <el-form-item label="试卷名称" prop="name">
          <el-input v-model="manualForm.name" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="manualForm.courseId" placeholder="请选择课程" @change="handleManualCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="manualForm.duration" :min="30" :max="180" />
        </el-form-item>
        <el-form-item label="试卷说明">
          <el-input v-model="manualForm.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
        </el-form-item>
        
        <!-- 选择题目 -->
        <el-form-item label="选择题目">
          <el-tabs v-model="questionTab">
            <el-tab-pane label="单选题" name="single">
              <el-checkbox-group v-model="manualForm.questionIds">
                <div v-for="q in singleQuestions" :key="q.id" class="question-checkbox">
                  <el-checkbox :label="q.id">{{ q.title }}</el-checkbox>
                </div>
              </el-checkbox-group>
            </el-tab-pane>
            <el-tab-pane label="多选题" name="multi">
              <el-checkbox-group v-model="manualForm.questionIds">
                <div v-for="q in multiQuestions" :key="q.id" class="question-checkbox">
                  <el-checkbox :label="q.id">{{ q.title }}</el-checkbox>
                </div>
              </el-checkbox-group>
            </el-tab-pane>
            <el-tab-pane label="判断题" name="judge">
              <el-checkbox-group v-model="manualForm.questionIds">
                <div v-for="q in judgeQuestions" :key="q.id" class="question-checkbox">
                  <el-checkbox :label="q.id">{{ q.title }}</el-checkbox>
                </div>
              </el-checkbox-group>
            </el-tab-pane>
            <el-tab-pane label="填空题" name="fill">
              <el-checkbox-group v-model="manualForm.questionIds">
                <div v-for="q in fillQuestions" :key="q.id" class="question-checkbox">
                  <el-checkbox :label="q.id">{{ q.title }}</el-checkbox>
                </div>
              </el-checkbox-group>
            </el-tab-pane>
            <el-tab-pane label="简答题" name="essay">
              <el-checkbox-group v-model="manualForm.questionIds">
                <div v-for="q in essayQuestions" :key="q.id" class="question-checkbox">
                  <el-checkbox :label="q.id">{{ q.title }}</el-checkbox>
                </div>
              </el-checkbox-group>
            </el-tab-pane>
          </el-tabs>
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
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="autoForm.courseId" placeholder="请选择课程" @change="handleAutoCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
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
            <el-form-item label="单选题分值">
              <el-input-number v-model="autoForm.singleScore" :min="1" :max="20" />
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
            <el-form-item label="多选题分值">
              <el-input-number v-model="autoForm.multiScore" :min="1" :max="20" />
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
            <el-form-item label="判断题分值">
              <el-input-number v-model="autoForm.judgeScore" :min="1" :max="20" />
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
            <el-form-item label="填空题分值">
              <el-input-number v-model="autoForm.fillScore" :min="1" :max="20" />
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
            <el-form-item label="简答题分值">
              <el-input-number v-model="autoForm.essayScore" :min="1" :max="50" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="autoDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAutoSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import paperApi from '@/api/paper'
import courseApi from '@/api/course'
import questionApi from '@/api/question'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const paperList = ref([])
const courses = ref([])
const allQuestions = ref([])

const questionTab = ref('single')

const manualDialogVisible = ref(false)
const manualForm = ref({
  name: '',
  courseId: null,
  courseName: '',
  teacherId: userInfo.value.id,
  teacherName: userInfo.value.name,
  duration: 60,
  description: '',
  questionIds: []
})

const manualRules = {
  name: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

const manualFormRef = ref(null)

const autoDialogVisible = ref(false)
const autoForm = ref({
  name: '',
  courseId: null,
  courseName: '',
  teacherId: userInfo.value.id,
  teacherName: userInfo.value.name,
  duration: 60,
  description: '',
  singleCount: 10,
  singleScore: 5,
  multiCount: 5,
  multiScore: 10,
  judgeCount: 10,
  judgeScore: 5,
  fillCount: 5,
  fillScore: 10,
  essayCount: 2,
  essayScore: 20
})

const autoRules = {
  name: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

const autoFormRef = ref(null)

const singleQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '单选题'))
const multiQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '多选题'))
const judgeQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '判断题'))
const fillQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '填空题'))
const essayQuestions = computed(() => allQuestions.value.filter(q => q.typeName === '简答题'))

onMounted(async () => {
  await loadCourses()
  await loadPaperList()
})

const loadCourses = async () => {
  try {
    const res = await courseApi.getByTeacherId(userInfo.value.id)
    courses.value = res.data || []
  } catch (error) {
    console.error('获取课程失败', error)
  }
}

const loadPaperList = async () => {
  loading.value = true
  try {
    const res = await paperApi.getByTeacherId(userInfo.value.id)
    paperList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleManualCourseChange = async (courseId) => {
  const course = courses.value.find(c => c.id === courseId)
  if (course) {
    manualForm.value.courseName = course.name
    // 加载该课程的题目
    try {
      const res = await questionApi.getByCourseId(courseId)
      allQuestions.value = res.data || []
    } catch (error) {
      console.error('获取题目失败', error)
    }
  }
}

const handleAutoCourseChange = (courseId) => {
  const course = courses.value.find(c => c.id === courseId)
  if (course) {
    autoForm.value.courseName = course.name
  }
}

const handleManualCreate = () => {
  manualForm.value = {
    name: '',
    courseId: null,
    courseName: '',
    teacherId: userInfo.value.id,
    teacherName: userInfo.value.name,
    duration: 60,
    description: '',
    questionIds: []
  }
  allQuestions.value = []
  manualDialogVisible.value = true
}

const handleAutoCreate = () => {
  autoForm.value = {
    name: '',
    courseId: null,
    courseName: '',
    teacherId: userInfo.value.id,
    teacherName: userInfo.value.name,
    duration: 60,
    description: '',
    singleCount: 10,
    singleScore: 5,
    multiCount: 5,
    multiScore: 10,
    judgeCount: 10,
    judgeScore: 5,
    fillCount: 5,
    fillScore: 10,
    essayCount: 2,
    essayScore: 20
  }
  autoDialogVisible.value = true
}

const handleManualSave = async () => {
  await manualFormRef.value.validate()
  if (manualForm.value.questionIds.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }
  
  loading.value = true
  try {
    await paperApi.manualCreate(manualForm.value)
    ElMessage.success('组卷成功')
    manualDialogVisible.value = false
    loadPaperList()
  } finally {
    loading.value = false
  }
}

const handleAutoSave = async () => {
  await autoFormRef.value.validate()
  
  loading.value = true
  try {
    await paperApi.autoCreate(autoForm.value)
    ElMessage.success('组卷成功')
    autoDialogVisible.value = false
    loadPaperList()
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  // 查看试卷详情
  ElMessage.info('查看试卷详情功能待实现')
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
.teacher-paper {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-checkbox {
  margin: 10px 0;
}
</style>