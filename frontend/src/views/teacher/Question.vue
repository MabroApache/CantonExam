<template>
  <div class="teacher-question">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
          <el-button type="primary" @click="handleAdd">新增题目</el-button>
        </div>
      </template>
      
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
      
      <el-table :data="questionList" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="typeName" label="题型" width="100" />
        <el-table-column prop="title" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="row.difficulty === 1 ? 'success' : row.difficulty === 2 ? 'warning' : 'danger'">
              {{ row.difficulty === 1 ? '简单' : row.difficulty === 2 ? '中等' : '困难' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" style="color: #F56C6C">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
      <el-form :model="questionForm" :rules="rules" ref="questionFormRef" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="questionForm.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型" prop="typeId">
          <el-select v-model="questionForm.typeId" placeholder="请选择题型" @change="handleTypeChange">
            <el-option v-for="type in questionTypes" :key="type.id" :label="type.name" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" prop="title">
          <el-input v-model="questionForm.title" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        
        <!-- 选项（单选、多选） -->
        <el-form-item label="选项A" v-if="showOptions">
          <el-input v-model="questionForm.optionA" placeholder="请输入选项A" />
        </el-form-item>
        <el-form-item label="选项B" v-if="showOptions">
          <el-input v-model="questionForm.optionB" placeholder="请输入选项B" />
        </el-form-item>
        <el-form-item label="选项C" v-if="showOptions">
          <el-input v-model="questionForm.optionC" placeholder="请输入选项C" />
        </el-form-item>
        <el-form-item label="选项D" v-if="showOptions">
          <el-input v-model="questionForm.optionD" placeholder="请输入选项D" />
        </el-form-item>
        <el-form-item label="选项E" v-if="showMoreOptions">
          <el-input v-model="questionForm.optionE" placeholder="请输入选项E" />
        </el-form-item>
        <el-form-item label="选项F" v-if="showMoreOptions">
          <el-input v-model="questionForm.optionF" placeholder="请输入选项F" />
        </el-form-item>
        
        <el-form-item label="正确答案" prop="answer">
          <el-input v-model="questionForm.answer" placeholder="单选/判断填A/B/C/D或正确/错误，多选填ABCD，填空/简答填答案" />
        </el-form-item>
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import questionApi from '@/api/question'
import courseApi from '@/api/course'
import questionTypeApi from '@/api/questionType'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const questionList = ref([])
const courses = ref([])
const questionTypes = ref([])

const searchForm = ref({
  courseId: null,
  typeId: null,
  difficulty: null,
  teacherId: userInfo.value.id
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const questionForm = ref({
  id: null,
  courseId: null,
  courseName: '',
  typeId: null,
  typeName: '',
  teacherId: userInfo.value.id,
  teacherName: userInfo.value.name,
  title: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  optionE: '',
  optionF: '',
  answer: '',
  analysis: '',
  score: 5,
  difficulty: 1,
  status: 1
})

const rules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  typeId: [{ required: true, message: '请选择题型', trigger: 'change' }],
  title: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  answer: [{ required: true, message: '请输入正确答案', trigger: 'blur' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }]
}

const questionFormRef = ref(null)

const showOptions = computed(() => {
  const typeName = questionTypes.value.find(t => t.id === questionForm.value.typeId)?.name
  return typeName === '单选题' || typeName === '多选题'
})

const showMoreOptions = computed(() => {
  const typeName = questionTypes.value.find(t => t.id === questionForm.value.typeId)?.name
  return typeName === '多选题'
})

onMounted(async () => {
  await loadCourses()
  await loadQuestionTypes()
  await loadQuestionList()
})

const loadCourses = async () => {
  try {
    const res = await courseApi.getByTeacherId(userInfo.value.id)
    courses.value = res.data || []
  } catch (error) {
    console.error('获取课程失败', error)
  }
}

const loadQuestionTypes = async () => {
  try {
    const res = await questionTypeApi.getList()
    questionTypes.value = res.data || []
  } catch (error) {
    console.error('获取题型失败', error)
  }
}

const loadQuestionList = async () => {
  loading.value = true
  try {
    const res = await questionApi.getByTeacherId(userInfo.value.id)
    questionList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleCourseChange = (courseId) => {
  const course = courses.value.find(c => c.id === courseId)
  if (course) {
    questionForm.value.courseName = course.name
  }
}

const handleTypeChange = (typeId) => {
  const type = questionTypes.value.find(t => t.id === typeId)
  if (type) {
    questionForm.value.typeName = type.name
  }
}

const handleSearch = () => {
  loadQuestionList()
}

const handleReset = () => {
  searchForm.value = {
    courseId: null,
    typeId: null,
    difficulty: null,
    teacherId: userInfo.value.id
  }
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增题目'
  questionForm.value = {
    id: null,
    courseId: null,
    courseName: '',
    typeId: null,
    typeName: '',
    teacherId: userInfo.value.id,
    teacherName: userInfo.value.name,
    title: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    optionE: '',
    optionF: '',
    answer: '',
    analysis: '',
    score: 5,
    difficulty: 1,
    status: 1
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
.teacher-question {
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