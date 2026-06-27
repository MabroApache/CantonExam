<template>
  <div class="question-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库信息管理</span>
          <el-button type="primary" @click="handleAdd">新增题目</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 题目列表 -->
      <el-table :data="questionList" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="typeName" label="题型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.typeCode)">
              {{ row.typeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyTag(row.difficulty)">
              {{ getDifficultyText(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" style="color: #F56C6C">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
      <el-form :model="questionForm" :rules="rules" ref="questionFormRef" label-width="100px">
        <el-form-item label="题型" prop="typeId">
          <el-select v-model="questionForm.typeId" placeholder="请选择题型" style="width: 100%" @change="handleTypeChange">
            <el-option v-for="type in questionTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属出题人" prop="creatorId">
          <el-select v-model="questionForm.creatorId" placeholder="请选择出题人" style="width: 100%">
            <el-option v-for="creator in creatorList" :key="creator.id" :label="creator.name" :value="creator.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" prop="title">
          <el-input v-model="questionForm.title" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        
        <!-- 判断题 -->
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
        
        <!-- 单选题 -->
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
        
        <!-- 多选题 -->
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
        
        <!-- 填空题/简答题 -->
        <template v-else>
          <el-form-item label="正确答案" prop="answer">
            <el-input v-model="questionForm.answer" type="textarea" :rows="3" placeholder="请输入正确答案" />
          </el-form-item>
        </template>
        
        <el-form-item label="答案解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="3" placeholder="请输入答案解析" />
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import questionApi from '@/api/question'
import questionTypeApi from '@/api/questionType'
import creatorApi from '@/api/creator'
import departmentApi from '@/api/department'

const loading = ref(false)
const questionList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const questionTypes = ref([])
const creatorList = ref([])
const departmentList = ref([])

const searchForm = ref({
  typeId: null,
  difficulty: null,
  departmentId: null,
  tags: []
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const questionForm = ref({
  id: null,
  typeId: null,
  typeName: '',
  creatorId: null,
  creatorName: '',
  departmentId: null,
  departmentName: '',
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
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }],
  optionA: [{ required: true, message: '请输入选项A', trigger: 'blur' }],
  optionB: [{ required: true, message: '请输入选项B', trigger: 'blur' }],
  optionC: [{ required: true, message: '请输入选项C', trigger: 'blur' }],
  optionD: [{ required: true, message: '请输入选项D', trigger: 'blur' }]
}

const questionFormRef = ref(null)

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
  await loadCreatorList()
  await loadDepartmentList()
  await loadQuestionList()
})

const loadQuestionList = async () => {
  loading.value = true
  try {
    const res = await questionApi.search(searchForm.value)
    questionList.value = res.data || []
    total.value = res.data?.length || 0
  } finally {
    loading.value = false
  }
}

const loadQuestionTypes = async () => {
  try {
    const res = await questionTypeApi.getList()
    questionTypes.value = res.data || []
  } catch (error) {
    console.error('加载题型失败', error)
  }
}

const loadCreatorList = async () => {
  try {
    const res = await creatorApi.getList()
    creatorList.value = res.data || []
  } catch (error) {
    console.error('加载出题人列表失败', error)
  }
}

const loadDepartmentList = async () => {
  try {
    const res = await departmentApi.getList()
    departmentList.value = res.data || []
  } catch (error) {
    console.error('加载部门列表失败', error)
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

const getTypeTag = (code) => {
  const typeMap = {
    'single': 'primary',
    'multi': 'success',
    'judge': 'warning',
    'fill': 'info',
    'essay': 'danger'
  }
  return typeMap[code] || 'info'
}

const getDifficultyTag = (difficulty) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return tagMap[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const textMap = {
    1: '简单',
    2: '中等',
    3: '困难'
  }
  return textMap[difficulty] || '未知'
}

const handleSearch = () => {
  pageNum.value = 1
  loadQuestionList()
}

const handleReset = () => {
  searchForm.value = {
    typeId: null,
    difficulty: null,
    departmentId: null,
    tags: []
  }
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadQuestionList()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadQuestionList()
}

const handleAdd = () => {
  dialogTitle.value = '新增题目'
  questionForm.value = {
    id: null,
    typeId: null,
    typeName: '',
    creatorId: null,
    departmentId: null,
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
  questionForm.value = { ...row }
  if (currentTypeName.value === '多选题') {
    const answer = row.answer || ''
    multiAnswerOptions.value = answer.split('').filter(c => ['A', 'B', 'C', 'D'].includes(c))
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await questionFormRef.value.validate()
  loading.value = true
  try {
    if (questionForm.value.id) {
      await questionApi.update(questionForm.value)
      ElMessage.success('更新成功')
    } else {
      await questionApi.add(questionForm.value)
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
.question-manage {
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
</style>