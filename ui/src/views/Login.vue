<template>
  <div class="page">
    <el-card class="login-card" header="用户登录">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码" 
          />
        </el-form-item>
        
        <el-form-item label="身份" prop="role">
          <el-select v-model="form.role" placeholder="请选择身份">
            <el-option label="乘客" value="PASSENGER" />
            <el-option label="司机" value="DRIVER" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submit" :loading="loading">登录</el-button>
        </el-form-item>
        
        <el-form-item v-if="error" style="margin-bottom: 0">
          <el-alert 
            title="登录失败" 
            :description="error" 
            type="error" 
            show-icon 
            :closable="false" 
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import { login } from '../api/login'
import { useAuthStore } from '../stores/auth'
import router from '../router'
import type { FormInstance } from 'element-plus'

export default defineComponent({
  setup() {
    const formRef = ref<FormInstance>()
    const loading = ref(false)
    const error = ref('')
    const auth = useAuthStore()

    const form = reactive({
      username: '',
      password: '',
      role: 'PASSENGER'
    })

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      role: [{ required: true, message: '请选择身份', trigger: 'change' }]
    }

    const submit = async () => {
      try {
        await formRef.value?.validate()
        loading.value = true
        error.value = ''
        
        const res = await login(form.username, form.password, form.role)
        auth.setAuth({
          token: res.data.token,
          roles: res.data.roles
        })

        if (form.role === 'PASSENGER') {
          router.push('/passenger')
        } else {
          router.push('/driver')
        }
      } catch (err: any) {
        if (err.name === 'Error') {
          error.value = '用户名或密码错误'
        }
      } finally {
        loading.value = false
      }
    }

    return { form, rules, formRef, loading, error, submit }
  }
})
</script>

<style scoped>
.login-card {
  max-width: 400px;
  margin: 50px auto;
}
</style>