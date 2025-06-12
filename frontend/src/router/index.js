import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Profile from '@/views/Profile.vue'
import Devices from '@/views/Devices.vue'
import Chat from '@/views/Chat.vue'    // 新增
import AboutView from '../views/AboutView.vue'
import PolicyView from '@/views/PolicyView.vue'
import FeedbackView from '@/views/FeedbackView.vue'

const routes = [
  { path: '/login', component: Login },
  {
    path: '/dashboard',
    component: Dashboard,
    meta: {
      keepAlive: true // 添加此元标记
    }
  },
  {
    path: '/about',
    name: 'about',
    component: AboutView,
    meta: {
      title: '关于系统'
    }
  },
  {
    path: '/policies/:type',
    name: 'policy',
    component: PolicyView,
    props: true
  },
  {
    path: '/feedback',
    name: 'feedback',
    component: FeedbackView
  },
  { path: '/profile', component: Profile },
  { path: '/devices', component: Devices },
  { path: '/chat', component: Chat },   // 智能问答
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加全局路由守卫
router.beforeEach((to, from, next) => {
  const authToken = localStorage.getItem('authToken')
  if (!authToken && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
