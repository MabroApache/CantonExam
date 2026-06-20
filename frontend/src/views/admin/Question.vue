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
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable>
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
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
        <el-table-column prop="courseName" label="课程" width="150" />
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
      <el-form :model="questionForm" :rules="rules" ref="questionFormRef" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="questionForm.courseId" placeholder="请选择课程" style="width: 100%" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型" prop="typeId">
          <el-select v-model="questionForm.typeId" placeholder="请选择题型" style="width: 100%" @change="handleTypeChange">
            <el-option v-for="type in questionTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" prop="title">
          <el-input v-model="questionForm.title" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        
        <!-- 单选/多选题选项 -->
        <template v-if="showOptions">
          <el-form-item label="选项A">
            <el-input v-model="questionForm.optionA" placeholder="请输入选项A" />
          </el-form-item>
          <el-form-item label="选项B">
            <el-input v-model="questionForm.optionB" placeholder="请输入选项B" />
          </el-form-item>
          <el-form-item label="选项C">
            <el-input v-model="questionForm.optionC" placeholder="请输入选项C" />
          </el-form-item>
          <el-form-item label="选项D">
            <el-input v-model="questionForm.optionD" placeholder="请输入选项D" />
          </el-form-item>
        </template>
        
        <el-form-item label="答案" prop="answer">
          <el-input v-model="questionForm.answer" :type="isEssay ? 'textarea' : 'text'" :rows="isEssay ? 3 : 1" placeholder="请输入答案" />
          <span v-if="showOptions" style="color: #909399; font-size: 12px; margin-top: 5px; display: block;">
            单选题请输入单个字母（如：A），多选题请输入多个字母（如：AB）
          </span>
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="3" placeholder="请输入解析" />
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="questionForm.difficulty" placeholder="请选择难度" style="width: 100%">
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import questionApi from '@/api/question'
import courseApi from '@/api/course'
import questionTypeApi from '@/api/questionType'

const loading = ref(false)
const questionList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const courses = ref([])
const questionTypes = ref([])

const searchForm = ref({
  courseId: null,
  typeId: null,
  difficulty: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const questionForm = ref({
  id: null,
  courseId: null,
  typeId: null,
  title: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: '',
  analysis: '',
  score: 5,
  difficulty: 2
})

const rules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  typeId: [{ required: true, message: '请选择题型', trigger: 'change' }],
  title: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  answer: [{ required: true, message: '请输入答案', trigger: 'blur' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }]
}

const questionFormRef = ref(null)

const showOptions = computed(() => {
  const type = questionTypes.value.find(t => t.id === questionForm.value.typeId)
  return type && (type.code === 'single' || type.code === 'multi')
})

const isEssay = computed(() => {
  const type = questionTypes.value.find(t => t.id === questionForm.value.typeId)
  return type && type.code === 'essay'
})

onMounted(() => {
  loadQuestionList()
  loadCourses()
  loadQuestionTypes()
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

const loadCourses = async () => {
  try {
    const res = await courseApi.getList()
    courses.value = res.data || []
  } catch (error) {
    console.error('加载课程失败', error)
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

const handleCourseChange = () => {
  // 课程变更时的处理
}

const handleTypeChange = () => {
  // 清空选项
  questionForm.value.optionA = ''
  questionForm.value.optionB = ''
  questionForm.value.optionC = ''
  questionForm.value.optionD = ''
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
    courseId: null,
    typeId: null,
    difficulty: null
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
    courseId: null,
    typeId: null,
    title: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    answer: '',
    analysis: '',
    score: 5,
    difficulty: 2
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑题目'
  questionForm.value = { ...row }
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
</style>