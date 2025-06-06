import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Profile from '@/views/Profile.vue'
import Devices from '@/views/Devices.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/dashboard', component: Dashboard },
  { path: '/profile', component: Profile },
  { path: '/devices', component: Devices },
  { path: '/', redirect: '/login' } // 修改根路径重定向为登录页面
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加全局路由守卫，未登录时跳转到 /login
router.beforeEach((to, from, next) => {
  const authToken = localStorage.getItem('authToken')
  if (!authToken && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router