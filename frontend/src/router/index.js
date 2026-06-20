import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/home',
    meta: { role: 'admin' },
    children: [
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('@/views/admin/Home.vue'),
        meta: { title: '系统首页' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/admin/Notice.vue'),
        meta: { title: '系统公告' }
      },
      {
        path: 'exam',
        name: 'ExamManage',
        component: () => import('@/views/admin/Exam.vue'),
        meta: { title: '考试安排' }
      },
      {
        path: 'questionType',
        name: 'QuestionType',
        component: () => import('@/views/admin/QuestionType.vue'),
        meta: { title: '题型信息' }
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('@/views/admin/Course.vue'),
        meta: { title: '课程信息' }
      },
      {
        path: 'question',
        name: 'Question',
        component: () => import('@/views/admin/Question.vue'),
        meta: { title: '题库信息' }
      },
      {
        path: 'paper',
        name: 'Paper',
        component: () => import('@/views/admin/Paper.vue'),
        meta: { title: '试卷信息' }
      },
      {
        path: 'share',
        name: 'ShareManage',
        component: () => import('@/views/admin/Share.vue'),
        meta: { title: '交流分享' }
      },
      {
        path: 'score',
        name: 'ScoreManage',
        component: () => import('@/views/admin/Score.vue'),
        meta: { title: '成绩信息' }
      },
      {
        path: 'adminInfo',
        name: 'AdminInfo',
        component: () => import('@/views/admin/AdminInfo.vue'),
        meta: { title: '管理员信息' }
      },
      {
        path: 'teacherInfo',
        name: 'TeacherInfo',
        component: () => import('@/views/admin/TeacherInfo.vue'),
        meta: { title: '教师信息' }
      },
      {
        path: 'studentInfo',
        name: 'StudentInfo',
        component: () => import('@/views/admin/StudentInfo.vue'),
        meta: { title: '学生信息' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/teacher',
    name: 'TeacherLayout',
    component: () => import('@/layouts/TeacherLayout.vue'),
    redirect: '/teacher/home',
    meta: { role: 'teacher' },
    children: [
      {
        path: 'home',
        name: 'TeacherHome',
        component: () => import('@/views/teacher/Home.vue'),
        meta: { title: '系统首页' }
      },
      {
        path: 'course',
        name: 'TeacherCourse',
        component: () => import('@/views/teacher/Course.vue'),
        meta: { title: '课程信息' }
      },
      {
        path: 'question',
        name: 'TeacherQuestion',
        component: () => import('@/views/teacher/Question.vue'),
        meta: { title: '题库信息' }
      },
      {
        path: 'paper',
        name: 'TeacherPaper',
        component: () => import('@/views/teacher/Paper.vue'),
        meta: { title: '试卷信息' }
      },
      {
        path: 'exam',
        name: 'TeacherExam',
        component: () => import('@/views/teacher/Exam.vue'),
        meta: { title: '考试安排' }
      },
      {
        path: 'grade',
        name: 'Grade',
        component: () => import('@/views/teacher/Grade.vue'),
        meta: { title: '阅卷打分' }
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/student',
    name: 'StudentLayout',
    component: () => import('@/layouts/StudentLayout.vue'),
    redirect: '/student/home',
    meta: { role: 'student' },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/Home.vue'),
        meta: { title: '系统首页' }
      },
      {
        path: 'exam',
        name: 'StudentExam',
        component: () => import('@/views/student/Exam.vue'),
        meta: { title: '在线考试' }
      },
      {
        path: 'exam/:examId',
        name: 'ExamDetail',
        component: () => import('@/views/student/ExamDetail.vue'),
        meta: { title: '答题页面' }
      },
      {
        path: 'score',
        name: 'StudentScore',
        component: () => import('@/views/student/Score.vue'),
        meta: { title: '我的成绩' }
      },
      {
        path: 'share',
        name: 'MyShare',
        component: () => import('@/views/student/MyShare.vue'),
        meta: { title: '我的分享' }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/student/Forum.vue'),
        meta: { title: '交流论坛' }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token')
  const role = userStore.role || localStorage.getItem('role')

  // 设置页面标题
  document.title = to.meta.title || '广交会在线考试系统'

  // 登录页面直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 未登录跳转到登录页
  if (!token) {
    next('/login')
    return
  }

  // 权限验证
  const requiredRole = to.meta.role
  if (requiredRole && role !== requiredRole) {
    next('/login')
    return
  }

  next()
})

export default router