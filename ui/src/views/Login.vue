<template>
  <div class="page-container">
    <!-- 背景装饰 -->
    <div class="background-decoration"></div>

    <el-card class="login-card" :header="isLogin ? '用户登录' : '用户注册'">
      <div class="card-header-icon">
  <el-icon :size="36" :component="isLogin ? Lock : User" />
      </div>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="80px"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            clearable
            show-password
          />
        </el-form-item>

        <el-form-item
          label="确认密码"
          prop="confirmPassword"
          v-if="!isLogin"
        >
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            clearable
            show-password
          />
        </el-form-item>

        <!-- 错误提示 -->
        <el-form-item v-if="error" class="error-message">
          <el-alert
            :description="error"
            type="error"
            show-icon
            :closable="false"
            effect="light"
          />
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button
            type="primary"
            @click="submit"
            :loading="loading"
            class="main-action-btn"
            :disabled="loading"
          >
            {{ isLogin ? '登录' : '注册' }}
          </el-button>

          <el-button text @click="toggleMode" :disabled="loading">
            {{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed, nextTick } from 'vue'
import { login, register, getUserByUsername } from '../api/user'
import { useAuthStore } from '../stores/auth'
import { useUserStore } from '../stores/userStore'
import router from '../router'
import { ElMessage, ElIcon } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

export default defineComponent({
  components: { ElIcon, Lock, User },
  setup() {

    // ❗❗❗ 正确的位置
    const auth = useAuthStore()
    const userStore = useUserStore()

    const formRef = ref<FormInstance>()
    const isLogin = ref(true)

    const loginLoading = ref(false)
    const registerLoading = ref(false)
    const error = ref('')

    const form = reactive({
      username: '',
      password: '',
      confirmPassword: ''
    })

    const rules = computed(() => ({
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3-20 个字符之间', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6-20 个字符之间', trigger: 'blur' }
      ],
      confirmPassword: isLogin.value
        ? []
        : [
            { required: true, message: '请确认密码', trigger: 'blur' },
            {
              validator(rule: any, value: string, callback: any) {
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
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    const submit = async () => {
      try {
        await formRef.value?.validate()
        error.value = ''
        if (isLogin.value) {
          loginLoading.value = true
          await handleLogin()
        } else {
          registerLoading.value = true
          await handleRegister()
        }
      } catch (err: any) {
        error.value =
          err.response?.data?.message || err.message || (isLogin.value ? '登录失败' : '注册失败')
      } finally {
        loginLoading.value = false
        registerLoading.value = false
        nextTick(() =>
          document.querySelector('.error-message')?.scrollIntoView({ behavior: 'smooth' })
        )
      }
    }

    const handleLogin = async () => {
      const res = await login(form.username, form.password)

      auth.setAuth(res.data.data.token)
      ElMessage.success('登录成功，正在跳转...')

      const userResp = await getUserByUsername(form.username)
      if (userResp.data && userResp.data.data) {
        userStore.setUserInfo(userResp.data.data)
      }

      setTimeout(() => {
        router.push('/ride')
      }, 80)
    }

    const handleRegister = async () => {
      await register(form.username, form.password)
      ElMessage.success('注册成功，请登录')
      toggleMode()
    }

    const loading = computed(() =>
      isLogin.value ? loginLoading.value : registerLoading.value
    )

    return {
      form,
      rules,
      formRef,
      loading,
      error,
      submit,
      isLogin,
      toggleMode,
      Lock,
      User
    }
  }
})
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
  position: relative;
}

.background-decoration {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 20%, rgba(64, 158, 255, 0.1) 0%, transparent 40%),
              radial-gradient(circle at 70% 60%, rgba(64, 158, 255, 0.1) 0%, transparent 40%);
  z-index: 0;
}

.login-card {
  max-width: 420px;
  width: 100%;
  min-height: 480px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.card-header-icon {
  font-size: 36px;
  color: #409eff;
  margin-bottom: 20px;
  text-align: center;
}

.login-form {
  padding: 0 30px 30px;
}

.error-message {
  margin-bottom: 15px;
  animation: shake 0.5s;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
  20%, 40%, 60%, 80% { transform: translateX(5px); }
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.main-action-btn {
  width: 100%;
  border-radius: 8px;
}

/* 移动端适配 */
@media (max-width: 576px) {
  .login-card {
    margin: 10px auto;
    min-height: 440px;
  }
  .login-form {
    padding: 10px 15px 20px;
  }
}
</style>
