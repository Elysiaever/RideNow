<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>司机控制台</span>
          <el-button type="primary" text @click="logout">退出登录</el-button>
        </div>
      </template>
      
      <el-descriptions column="1">
        <el-descriptions-item label="欢迎信息">
          欢迎使用共享出行系统（司机端）
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- 引入认证信息组件 -->
    <AuthInfo />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useAuthStore } from '../stores/auth'
import router from '../router'
import AuthInfo from './AuthInfo.vue'  // 导入组件

export default defineComponent({
  components: { AuthInfo },  // 注册组件
  setup() {
    const auth = useAuthStore()
    
    const logout = () => {
      auth.logout()
      router.push('/login')
    }
    
    return { logout }
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page {
  padding: 20px;
  max-width: 800px;  /* 加宽页面以展示更多信息 */
}
</style>