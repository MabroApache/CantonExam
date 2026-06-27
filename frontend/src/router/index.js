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
        path: 'department',
        name: 'Department',
        component: () => import('@/views/admin/Department.vue'),
        meta: { title: '部门管理' }
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
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/creator',
    name: 'CreatorLayout',
    component: () => import('@/layouts/CreatorLayout.vue'),
    redirect: '/creator/home',
    meta: { role: 'creator' },
    children: [
      {
        path: 'home',
        name: 'CreatorHome',
        component: () => import('@/views/creator/Home.vue'),
        meta: { title: '系统首页' }
      },
      {
        path: 'question',
        name: 'CreatorQuestion',
        component: () => import('@/views/creator/Question.vue'),
        meta: { title: '题库管理' }
      },
      {
        path: 'paper',
        name: 'CreatorPaper',
        component: () => import('@/views/creator/Paper.vue'),
        meta: { title: '试卷管理' }
      },
      {
        path: 'exam',
        name: 'CreatorExam',
        component: () => import('@/views/creator/Exam.vue'),
        meta: { title: '考试安排' }
      },
      {
        path: 'grade',
        name: 'Grade',
        component: () => import('@/views/creator/Grade.vue'),
        meta: { title: '阅卷打分' }
      },
      {
        path: 'profile',
        name: 'CreatorProfile',
        component: () => import('@/views/creator/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/candidate',
    name: 'CandidateLayout',
    component: () => import('@/layouts/CandidateLayout.vue'),
    redirect: '/candidate/home',
    meta: { role: 'candidate' },
    children: [
      {
        path: 'home',
        name: 'CandidateHome',
        component: () => import('@/views/candidate/Home.vue'),
        meta: { title: '系统首页' }
      },
      {
        path: 'exam',
        name: 'CandidateExam',
        component: () => import('@/views/candidate/Exam.vue'),
        meta: { title: '在线考试' }
      },
      {
        path: 'exam/:examId',
        name: 'ExamDetail',
        component: () => import('@/views/candidate/ExamDetail.vue'),
        meta: { title: '答题页面' }
      },
      {
        path: 'score',
        name: 'CandidateScore',
        component: () => import('@/views/candidate/Score.vue'),
        meta: { title: '我的成绩' }
      },
      {
        path: 'profile',
        name: 'CandidateProfile',
        component: () => import('@/views/candidate/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token || sessionStorage.getItem('token')
  const role = userStore.role || sessionStorage.getItem('role')

  document.title = to.meta.title || '广交会在线考试系统'

  if (to.path === '/login') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }

  const requiredRole = to.meta.role
  if (requiredRole && role !== requiredRole) {
    next('/login')
    return
  }

  next()
})

export default router