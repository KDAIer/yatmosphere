import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Profile from '@/views/Profile.vue'
import Devices from '@/views/Devices.vue'

const routes = [
  { path: '/login', component: Login },
  {
    path: '/dashboard',
    component: Dashboard,
    meta: {
      keepAlive: true // 添加此元标记
    }
  },

  { path: '/profile', component: Profile },
  { path: '/devices', component: Devices },
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
