import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import DriverDashboard from '../views/DriverDashboard.vue'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/driver', component: DriverDashboard, meta: { requiresAuth: true } },
    { path: '/ride', name: 'Ride', component: () => import('@/views/RideTest.vue'), meta: { requiresAuth: true } },
  ]
})

// 全局路由守卫
router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  if (to.path === '/login') {
    next()
    return
  }

  if (to.meta.requiresAuth) {
    // 直接读取 localStorage token，保证拿到最新值
    const token = auth.token || localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录')
      auth.logout()
      next('/login')
      return
    }


  }

  next()
})

export default router
