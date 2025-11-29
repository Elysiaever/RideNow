import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import PassengerDashboard from '../views/PassengerDashboard.vue'
import DriverDashboard from '../views/DriverDashboard.vue'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'  // 引入消息提示组件

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes = [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    {
      path: '/passenger',
      component: PassengerDashboard,
      meta: { requiresAuth: true, role: 'PASSENGER' }
    },
    {
      path: '/driver',
      component: DriverDashboard,
      meta: { requiresAuth: true, role: 'DRIVER' }
    },
    {
      path: "/ride",
      name: "Ride",
      component: () => import("@/views/RideTest.vue")
    },
  ]
})
router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth) {
    // 未登录状态拦截
    if (!auth.isLoggedIn) {
      ElMessage.warning('请先登录后再访问')  // 提示信息
      next('/login')
      return
    }

    // 权限不足拦截
    if (to.meta.role && !auth.roles.includes(to.meta.role as string)) {
      ElMessage.error('您没有权限访问该页面')  // 提示信息
      next('/')
      return
    }
  }

  next()
})
export default router
