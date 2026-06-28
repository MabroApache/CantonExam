<template>
  <div class="creator-question">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
          <el-button type="primary" @click="handleAdd">新增题目</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="题型">
          <el-select v-model="searchForm.typeId" placeholder="请选择题型" clearable>
            <el-option v-for="type in questionTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="请选择难度" clearable>
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="searchForm.tags" multiple placeholder="请选择标签" clearable>
            <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="questionList" style="width: 100%" v-loading="loading">
        <el-table-column prop="typeName" label="题型" width="100" />
        <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="tags" label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-for="tag in (row.tags || [])" :key="tag" size="small" style="margin-right: 5px">{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="row.difficulty === 1 ? 'success' : row.difficulty === 2 ? 'warning' : 'danger'">
              {{ row.difficulty === 1 ? '简单' : row.difficulty === 2 ? '中等' : '困难' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handlePreview(row)">预览</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" style="color: #F56C6C">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
      <el-form :model="questionForm" :rules="rules" ref="questionFormRef" label-width="100px">
        <el-form-item label="题型" prop="typeId">
          <el-select v-model="questionForm.typeId" placeholder="请选择题型" @change="handleTypeChange">
            <el-option v-for="type in questionTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="questionForm.tags" multiple allow-create filterable placeholder="请输入或选择标签">
            <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" prop="title">
          <el-input v-model="questionForm.title" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="upload-demo"
            action="/api/file/upload"
            :on-success="handleImageUploadSuccess"
            :on-error="handleUploadError"
            :show-file-list="false"
            accept="image/*"
          >
            <el-button type="primary" size="small">上传图片</el-button>
          </el-upload>
          <el-input v-model="questionForm.imageUrl" placeholder="或输入图片URL" style="margin-top: 10px" />
          <div v-if="questionForm.imageUrl" style="margin-top: 10px">
            <img :src="questionForm.imageUrl" style="max-width: 300px; max-height: 200px" />
          </div>
        </el-form-item>
        <el-form-item label="视频">
          <el-input v-model="questionForm.videoUrl" placeholder="请输入视频URL" />
          <div v-if="questionForm.videoUrl" style="margin-top: 10px">
            <video :src="questionForm.videoUrl" controls style="max-width: 300px; max-height: 200px"></video>
          </div>
        </el-form-item>

        <template v-if="currentTypeName === '判断题'">
          <el-form-item label="选项">
            <div class="option-row">
              <el-checkbox v-model="judgeOptions.correct" label="正确" />
              <el-checkbox v-model="judgeOptions.wrong" label="错误" />
            </div>
          </el-form-item>
          <el-form-item label="正确答案" prop="answer">
            <div class="answer-options">
              <el-radio v-model="questionForm.answer" label="正确">正确</el-radio>
              <el-radio v-model="questionForm.answer" label="错误">错误</el-radio>
            </div>
          </el-form-item>
        </template>

        <template v-else-if="currentTypeName === '单选题'">
          <el-form-item label="选项A" prop="optionA">
            <el-input v-model="questionForm.optionA" placeholder="请输入选项A" />
          </el-form-item>
          <el-form-item label="选项B" prop="optionB">
            <el-input v-model="questionForm.optionB" placeholder="请输入选项B" />
          </el-form-item>
          <el-form-item label="选项C" prop="optionC">
            <el-input v-model="questionForm.optionC" placeholder="请输入选项C" />
          </el-form-item>
          <el-form-item label="选项D" prop="optionD">
            <el-input v-model="questionForm.optionD" placeholder="请输入选项D" />
          </el-form-item>
          <el-form-item label="正确答案" prop="answer">
            <div class="answer-options">
              <el-radio v-model="questionForm.answer" label="A">A</el-radio>
              <el-radio v-model="questionForm.answer" label="B">B</el-radio>
              <el-radio v-model="questionForm.answer" label="C">C</el-radio>
              <el-radio v-model="questionForm.answer" label="D">D</el-radio>
            </div>
          </el-form-item>
        </template>

        <template v-else-if="currentTypeName === '多选题'">
          <el-form-item label="选项A" prop="optionA">
            <el-input v-model="questionForm.optionA" placeholder="请输入选项A" />
          </el-form-item>
          <el-form-item label="选项B" prop="optionB">
            <el-input v-model="questionForm.optionB" placeholder="请输入选项B" />
          </el-form-item>
          <el-form-item label="选项C" prop="optionC">
            <el-input v-model="questionForm.optionC" placeholder="请输入选项C" />
          </el-form-item>
          <el-form-item label="选项D" prop="optionD">
            <el-input v-model="questionForm.optionD" placeholder="请输入选项D" />
          </el-form-item>
          <el-form-item label="正确答案" prop="answer">
            <div class="answer-options">
              <el-checkbox v-model="multiAnswerOptions" label="A">A</el-checkbox>
              <el-checkbox v-model="multiAnswerOptions" label="B">B</el-checkbox>
              <el-checkbox v-model="multiAnswerOptions" label="C">C</el-checkbox>
              <el-checkbox v-model="multiAnswerOptions" label="D">D</el-checkbox>
            </div>
          </el-form-item>
        </template>

        <template v-else>
          <el-form-item label="正确答案" prop="answer">
            <el-input v-model="questionForm.answer" type="textarea" :rows="3" placeholder="请输入正确答案" />
          </el-form-item>
        </template>

        <el-form-item label="答案解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="3" placeholder="请输入答案解析" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="questionForm.difficulty">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog title="题目预览" v-model="previewVisible" width="700px">
      <div class="preview-item">
        <div class="preview-title">
          <span class="preview-type">[{{ previewQuestion.typeName }}]</span>
          <span>{{ previewQuestion.title }}</span>
        </div>
        <div v-if="previewQuestion.mediaUrl" class="preview-media">
          <img v-if="isImage(previewQuestion.mediaUrl)" :src="previewQuestion.mediaUrl" style="max-width: 100%" />
          <video v-else :src="previewQuestion.mediaUrl" controls style="max-width: 100%"></video>
        </div>
        <div v-if="previewQuestion.optionA" class="preview-options">
          <div>A. {{ previewQuestion.optionA }}</div>
          <div>B. {{ previewQuestion.optionB }}</div>
          <div>C. {{ previewQuestion.optionC }}</div>
          <div>D. {{ previewQuestion.optionD }}</div>
        </div>
        <div class="preview-answer">
          <p><strong>正确答案：</strong>{{ previewQuestion.answer }}</p>
          <p v-if="previewQuestion.analysis"><strong>解析：</strong>{{ previewQuestion.analysis }}</p>
        </div>
        <div v-if="previewQuestion.tags && previewQuestion.tags.length" class="preview-tags">
          <el-tag v-for="tag in previewQuestion.tags" :key="tag" size="small" style="margin-right: 5px">{{ tag }}</el-tag>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import questionApi from '@/api/question'
import questionTypeApi from '@/api/questionType'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const questionList = ref([])
const questionTypes = ref([])
const allTags = ref([])

const searchForm = ref({
  typeId: null,
  difficulty: null,
  tags: [],
  departmentId: userInfo.value.departmentId
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const questionForm = ref({
  id: null,
  typeId: null,
  typeName: '',
  creatorId: userInfo.value.id,
  creatorName: userInfo.value.name,
  departmentId: userInfo.value.departmentId,
  title: '',
  tags: [],
  imageUrl: '',
  videoUrl: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: '',
  analysis: '',
  difficulty: 1,
  status: 1
})

const judgeOptions = ref({
  correct: true,
  wrong: true
})

const multiAnswerOptions = ref([])

const rules = {
  typeId: [{ required: true, message: '请选择题型', trigger: 'change' }],
  title: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  answer: [{ required: true, message: '请选择正确答案', trigger: 'change' }],
  optionA: [{ required: true, message: '请输入选项A', trigger: 'blur' }],
  optionB: [{ required: true, message: '请输入选项B', trigger: 'blur' }],
  optionC: [{ required: true, message: '请输入选项C', trigger: 'blur' }],
  optionD: [{ required: true, message: '请输入选项D', trigger: 'blur' }]
}

const questionFormRef = ref(null)

const previewVisible = ref(false)
const previewQuestion = ref({})

const currentTypeName = computed(() => {
  const type = questionTypes.value.find(t => t.id === questionForm.value.typeId)
  return type ? type.name : ''
})

watch(() => questionForm.value.typeId, () => {
  if (currentTypeName.value === '多选题') {
    const currentAnswer = questionForm.value.answer || ''
    multiAnswerOptions.value = currentAnswer.split('').filter(c => ['A', 'B', 'C', 'D'].includes(c))
  }
})

watch(multiAnswerOptions, (newVal) => {
  questionForm.value.answer = newVal.sort().join('')
}, { deep: true })

onMounted(async () => {
  await loadQuestionTypes()
  await loadAllTags()
  await loadQuestionList()
})

const loadQuestionTypes = async () => {
  try {
    const res = await questionTypeApi.getList()
    questionTypes.value = res.data || []
  } catch (error) {
    console.error('获取题型失败', error)
  }
}

const loadAllTags = async () => {
  try {
    const res = await questionApi.search({ departmentId: userInfo.value.departmentId })
    const list = res.data || []
    const tagsSet = new Set()
    list.forEach(q => {
      const tags = parseTags(q.tags)
      tags.forEach(tag => tagsSet.add(tag))
    })
    allTags.value = Array.from(tagsSet)
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

const parseTags = (tagsStr) => {
  if (!tagsStr) return []
  if (Array.isArray(tagsStr)) return tagsStr
  if (typeof tagsStr === 'string') {
    if (tagsStr.startsWith('[')) {
      try {
        return JSON.parse(tagsStr)
      } catch (e) {
        return tagsStr.split(',').map(t => t.trim()).filter(t => t)
      }
    }
    return tagsStr.split(',').map(t => t.trim()).filter(t => t)
  }
  return []
}

const tagsToString = (tagsArr) => {
  if (!tagsArr || !Array.isArray(tagsArr) || tagsArr.length === 0) return ''
  return tagsArr.join(',')
}

const loadQuestionList = async () => {
  loading.value = true
  try {
    const searchParams = { ...searchForm.value }
    if (searchForm.value.tags && searchForm.value.tags.length > 0) {
      searchParams.tags = searchForm.value.tags.join(',')
    } else {
      searchParams.tags = ''
    }
    const res = await questionApi.search(searchParams)
    const list = res.data || []
    list.forEach(q => {
      q.tags = parseTags(q.tags)
    })
    questionList.value = list
  } finally {
    loading.value = false
  }
}

const handleTypeChange = (typeId) => {
  const type = questionTypes.value.find(t => t.id === typeId)
  if (type) {
    questionForm.value.typeName = type.name
    if (type.name === '多选题') {
      const currentAnswer = questionForm.value.answer || ''
      multiAnswerOptions.value = currentAnswer.split('').filter(c => ['A', 'B', 'C', 'D'].includes(c))
    }
  }
}

const handleSearch = () => {
  loadQuestionList()
}

const handleReset = () => {
  searchForm.value = {
    typeId: null,
    difficulty: null,
    tags: [],
    departmentId: userInfo.value.departmentId
  }
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增题目'
  questionForm.value = {
    id: null,
    typeId: null,
    typeName: '',
    creatorId: userInfo.value.id,
    creatorName: userInfo.value.name,
    departmentId: userInfo.value.departmentId,
    title: '',
    tags: [],
    mediaUrl: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    answer: '',
    analysis: '',
    score: 5,
    difficulty: 1,
    status: 1
  }
  multiAnswerOptions.value = []
  judgeOptions.value = { correct: true, wrong: true }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑题目'
  questionForm.value = { 
    ...row,
    tags: parseTags(row.tags)
  }
  if (currentTypeName.value === '多选题') {
    const answer = row.answer || ''
    multiAnswerOptions.value = answer.split('').filter(c => ['A', 'B', 'C', 'D'].includes(c))
  }
  dialogVisible.value = true
}

const handlePreview = (row) => {
  previewQuestion.value = {
    ...row,
    tags: parseTags(row.tags)
  }
  previewVisible.value = true
}

const isImage = (url) => {
  if (!url) return false
  return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(url)
}

const handleImageUploadSuccess = (response) => {
  if (response.code === 200) {
    questionForm.value.imageUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败，请重试')
}

const handleSave = async () => {
  await questionFormRef.value.validate()
  loading.value = true
  try {
    const submitData = {
      ...questionForm.value,
      tags: tagsToString(questionForm.value.tags)
    }
    if (questionForm.value.id) {
      await questionApi.update(submitData)
      ElMessage.success('更新成功')
    } else {
      await questionApi.add(submitData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadQuestionList()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该题目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await questionApi.delete(row.id)
    ElMessage.success('删除成功')
    loadQuestionList()
  }).catch(() => {})
}
</script>

<style scoped>
.creator-question {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.option-row {
  display: flex;
  gap: 20px;
}

.answer-options {
  display: flex;
  gap: 20px;
}

.preview-item {
  padding: 20px;
}

.preview-title {
  font-size: 16px;
  margin-bottom: 15px;
}

.preview-type {
  color: #409EFF;
  margin-right: 10px;
}

.preview-media {
  margin-bottom: 15px;
}

.preview-options {
  margin-left: 20px;
  margin-bottom: 15px;
  color: #606266;
}

.preview-options div {
  margin: 5px 0;
}

.preview-answer {
  margin-top: 15px;
}

.preview-answer p {
  margin: 8px 0;
}

.preview-tags {
  margin-top: 15px;
}
</style>