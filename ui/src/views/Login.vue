<template>
  <div class="page">
    <el-card class="login-card" :header="isLogin ? '用户登录' : '用户注册'">
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
        
        <!-- 注册时显示确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isLogin">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请确认密码" 
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submit" :loading="loading">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
          <el-button style="margin-left: 10px" @click="toggleMode">
            {{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}
          </el-button>
        </el-form-item>
        
        <el-form-item v-if="error" style="margin-bottom: 0">
          <el-alert 
            :title="isLogin ? '登录失败' : '注册失败'" 
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
import { defineComponent, ref, reactive, computed } from 'vue'
import { login } from '../api/user'
import { register } from '../api/user'
import { useAuthStore } from '../stores/auth'
import router from '../router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

export default defineComponent({
  setup() {
    const formRef = ref<FormInstance>()
    const loading = ref(false)
    const error = ref('')
    const auth = useAuthStore()
    const isLogin = ref(true)

    const form = reactive({
      username: '2352835',
      password: '2352835',
      confirmPassword: ''
    })

    const rules = computed(() => ({
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      confirmPassword: isLogin.value ? [] : [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { 
          validator: (rule: any, value: string, callback: any) => {
            if (value !== form.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }))

    const toggleMode = () => {
      isLogin.value = !isLogin.value
      error.value = ''
      formRef.value?.resetFields()
    }

    const submit = async () => {
      try {
        await formRef.value?.validate()
        loading.value = true
        error.value = ''
        
        if (isLogin.value) {
          // 登录逻辑 - 登录成功后跳转到Ride页面
          const res = await login(form.username, form.password)
          console.log(res.data)
          auth.setAuth({
            token: res.data.token,
            roles: res.data.roles || ['PASSENGER']
          })
          // 直接跳转到行程页面，不分角色
          router.push('/ride')
        } else {
          // 注册逻辑
          const res = await register(form.username, form.password)
          if (res.data.success) {
            ElMessage.success('注册成功，请登录')
            toggleMode()
          }
        }
      } catch (err: any) {
        if (err.name === 'Error') {
          error.value = isLogin.value ? '用户名或密码错误' : '注册失败，请检查信息'
        } else if (err.message) {
          error.value = err.message
        }
      } finally {
        loading.value = false
      }
    }

    return { 
      form, 
      rules, 
      formRef, 
      loading, 
      error, 
      submit,
      isLogin,
      toggleMode
    }
  }
})
</script>

<style scoped>
.login-card {
  max-width: 400px;
  margin: 50px auto;
}
</style>