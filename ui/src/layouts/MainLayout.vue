<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <h2>RideNow</h2>
          </router-link>
        </div>
        <nav class="nav">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            router
            :ellipsis="false"
            class="el-menu-demo"
          >
            <el-menu-item index="/app/ride" v-if="isPassenger">
              <el-icon><House /></el-icon>
              乘客端
            </el-menu-item>
            <el-menu-item index="/app/driver" v-if="isDriver">
              <el-icon><Van /></el-icon>
              司机端
            </el-menu-item>
            <el-menu-item index="/app/history" v-if="isLoggedIn">
              <el-icon><Document /></el-icon>
              行程历史
            </el-menu-item>
            <el-menu-item index="/app/profile" v-if="isLoggedIn">
              <el-icon><User /></el-icon>
              个人资料
            </el-menu-item>
          </el-menu>
        </nav>
        <div class="user-actions" v-if="isLoggedIn">
          <el-dropdown>
            <span class="el-dropdown-link">
              {{ userInfo.nickname || userInfo.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">个人资料</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="login-action" v-else>
          <el-button type="primary" @click="goToLogin">登录</el-button>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>&copy; 2026 RideNow. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUserStore } from '@/stores/userStore'
import { 
  House, 
  Van, 
  Document, 
  User, 
  ArrowDown 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const userStore = useUserStore()

// 计算属性
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const isPassenger = computed(() => userInfo.value.roles?.includes('USER'))
const isDriver = computed(() => userInfo.value.roles?.includes('DRIVER'))

// 根据当前路由设置激活的菜单项
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/app/ride') || path.startsWith('/ride')) return '/app/ride'
  if (path.startsWith('/app/driver') || path.startsWith('/driver')) return '/app/driver'
  if (path.startsWith('/app/history') || path.startsWith('/history')) return '/app/history'
  if (path.startsWith('/app/profile') || path.startsWith('/profile')) return '/app/profile'
  return path
})

// 方法
const goToLogin = () => {
  router.push('/login')
}

const goToProfile = () => {
  router.push('/app/profile')
}

const logout = () => {
  authStore.logout()
  userStore.clearUserInfo()
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo h2 {
  color: #409eff;
  margin: 0;
}

.nav {
  flex: 1;
  margin: 0 20px;
}

.nav :deep(.el-menu) {
  border-bottom: none;
  background: transparent;
}

.user-actions, .login-action {
  display: flex;
  align-items: center;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}

.footer {
  background: #333;
  color: #fff;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}
</style>