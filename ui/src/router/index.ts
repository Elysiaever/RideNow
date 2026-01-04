import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import MainLayout from '../layouts/MainLayout.vue'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', component: Login },
    { path: '/', component: Home }, // 首页路由
    {
      path: '/app',
      component: MainLayout,
      children: [
        {
          path: '/ride',
          name: 'Ride',
          component: () => import('@/views/RideTest.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/driver',
          name: 'Driver',
          component: () => import('@/views/DriverWorkBench.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/history',
          name: 'History',
          component: () => import('@/views/RideHistory.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/profile',
          name: 'Profile',
          component: () => import('@/views/Profile.vue'),
          meta: { requiresAuth: true }
        }
      ]
    },
    // 重定向：未匹配路径到登录页
    { path: '/:pathMatch(.*)*', redirect: '/login' }
  ]
})

// 全局路由守卫
router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  // 登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 首页无需登录
  if (to.path === '/') {
    next()
    return
  }

  // 需要登录的路由
  if (to.meta.requiresAuth) {
    const token = auth.token || localStorage.getItem('token')

    // 如果没有 token 或 token 已过期
    if (!token || auth.isTokenExpired()) {
      ElMessage.warning('登录已过期，请重新登录')
      auth.logout()
      next('/login')
      return
    }
  }

  next()
})

export default router
