<template>
  <el-card class="auth-card">
    <template #header>
      <div class="card-header">
        <span>认证信息详情</span>
      </div>
    </template>
    
    <el-descriptions column="1" border>
      <el-descriptions-item label="登录状态">
        <el-tag :type="isLoggedIn ? 'success' : 'danger'">
          {{ isLoggedIn ? '已登录' : '未登录' }}
        </el-tag>
      </el-descriptions-item>
      
      <el-descriptions-item label="Token">
        <el-input 
          v-model="token" 
          readonly 
          :rows="3" 
          type="textarea"
          placeholder="无令牌信息"
        />
      </el-descriptions-item>
      
      <el-descriptions-item label="角色权限">
        <div v-for="(role, index) in roles" :key="index" class="role-tag">
          <el-tag type="info">{{ role }}</el-tag>
        </div>
      </el-descriptions-item>
      
      <el-descriptions-item label="路由访问权限">
        <div>
          <el-tag :type="canAccessPassenger ? 'success' : 'warning'">
            乘客页面: {{ canAccessPassenger ? '允许访问' : '禁止访问' }}
          </el-tag>
        </div>
        <div style="margin-top: 5px;">
          <el-tag :type="canAccessDriver ? 'success' : 'warning'">
            司机页面: {{ canAccessDriver ? '允许访问' : '禁止访问' }}
          </el-tag>
        </div>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useAuthStore } from '../stores/auth'

export default defineComponent({
  setup() {
    const auth = useAuthStore()
    
    // 计算属性判断权限
    const canAccessPassenger = computed(() => 
      auth.isLoggedIn && auth.roles.includes('PASSENGER')
    )
    
    const canAccessDriver = computed(() => 
      auth.isLoggedIn && auth.roles.includes('DRIVER')
    )

    return {
      isLoggedIn: auth.isLoggedIn,
      token: auth.token,
      roles: auth.roles,
      canAccessPassenger,
      canAccessDriver
    }
  }
})
</script>

<style scoped>
.auth-card {
  margin-top: 20px;
}

.role-tag {
  display: inline-block;
  margin-right: 10px;
  margin-bottom: 5px;
}
</style>