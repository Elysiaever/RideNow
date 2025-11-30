<template>
  <div class="page">
    <el-card class="home-card">
      <h1>共享出行系统</h1>
      <el-button type="primary" @click="goToLogin">
        <el-icon><User /></el-icon>
        去登录
      </el-button>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { User } from '@element-plus/icons-vue'
import router from '../router'

export default defineComponent({
  components: { User },
  setup() {
    const goToLogin = () => {
      router.push('/login')
    }
    return { goToLogin }
  }
})
</script>

<style scoped>
.home-card {
  text-align: center;
  padding: 50px 20px;
}
h1 {
  margin-bottom: 30px;
}
</style>