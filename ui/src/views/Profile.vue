<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
        </div>
      </template>
      
      <div class="profile-content">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-change="handleAvatarChange"
            :auto-upload="false"
          >
            <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="avatar-text">
            <h3>{{ userInfo.nickname || userInfo.username }}</h3>
            <p>
              <el-tag 
                v-for="role in userInfo.roles" 
                :key="role" 
                :type="getRoleTagType(role)"
                size="small"
                style="margin-right: 8px;"
              >
                {{ getRoleDisplay(role) }}
              </el-tag>
            </p>
          </div>
        </div>

        <el-form 
          :model="userInfo" 
          :rules="rules" 
          ref="profileFormRef"
          label-width="100px"
          class="profile-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userInfo.username" disabled />
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="userInfo.nickname" placeholder="请输入昵称" />
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userInfo.email" placeholder="请输入邮箱" />
          </el-form-item>
          
          <el-form-item label="性别">
            <el-radio-group v-model="userInfo.gender">
              <el-radio label="MALE">男</el-radio>
              <el-radio label="FEMALE">女</el-radio>
              <el-radio label="OTHER">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="生日">
            <el-date-picker
              v-model="userInfo.birthday"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存资料</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="security-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>安全设置</span>
        </div>
      </template>
      
      <div class="security-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="登录密码">
            <span>已设置</span>
            <el-button size="small" type="primary" link @click="showChangePassword = true">修改</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="绑定手机">
            <span>{{ userInfo.phone || '未绑定' }}</span>
            <el-button size="small" type="primary" link @click="showBindPhone = true">修改</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="绑定邮箱">
            <span>{{ userInfo.email || '未绑定' }}</span>
            <el-button size="small" type="primary" link @click="showBindEmail = true">修改</el-button>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmNewPassword">
          <el-input v-model="passwordForm.confirmNewPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定手机对话框 -->
    <el-dialog v-model="showBindPhone" title="绑定/修改手机" width="500px">
      <el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef" label-width="100px">
        <el-form-item label="当前手机" v-if="userInfo.phone">
          <el-input v-model="userInfo.phone" disabled />
        </el-form-item>
        <el-form-item label="新手机号" prop="newPhone">
          <el-input v-model="phoneForm.newPhone" placeholder="请输入新手机号" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input">
            <el-input v-model="phoneForm.code" placeholder="请输入验证码" style="flex: 1;" />
            <el-button 
              type="primary" 
              :disabled="countdown > 0 || sendingCode" 
              @click="sendCode('phone')"
              style="margin-left: 10px;"
            >
              {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindPhone = false">取消</el-button>
        <el-button type="primary" @click="bindPhone" :loading="bindingPhone">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定邮箱对话框 -->
    <el-dialog v-model="showBindEmail" title="绑定/修改邮箱" width="500px">
      <el-form :model="emailForm" :rules="emailRules" ref="emailFormRef" label-width="100px">
        <el-form-item label="当前邮箱" v-if="userInfo.email">
          <el-input v-model="userInfo.email" disabled />
        </el-form-item>
        <el-form-item label="新邮箱" prop="newEmail">
          <el-input v-model="emailForm.newEmail" placeholder="请输入新邮箱" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input">
            <el-input v-model="emailForm.code" placeholder="请输入验证码" style="flex: 1;" />
            <el-button 
              type="primary" 
              :disabled="countdown > 0 || sendingCode" 
              @click="sendCode('email')"
              style="margin-left: 10px;"
            >
              {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindEmail = false">取消</el-button>
        <el-button type="primary" @click="bindEmail" :loading="bindingEmail">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadFile } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 类型定义
interface UserInfo {
  id?: string
  username?: string
  nickname?: string
  phone?: string
  email?: string
  roles?: string[]
  avatar?: string
  gender?: string
  birthday?: string
}

interface PasswordForm {
  currentPassword: string
  newPassword: string
  confirmNewPassword: string
}

interface PhoneForm {
  newPhone: string
  code: string
}

interface EmailForm {
  newEmail: string
  code: string
}

// 响应式数据
const userStore = useUserStore()
const authStore = useAuthStore()
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const phoneFormRef = ref<FormInstance>()
const emailFormRef = ref<FormInstance>()

const userInfo = ref<UserInfo>({
  id: userStore.userInfo.id,
  username: userStore.userInfo.username,
  nickname: userStore.userInfo.nickname,
  phone: userStore.userInfo.phone,
  email: userStore.userInfo.email,
  roles: userStore.userInfo.roles,
  avatar: userStore.userInfo.avatar || '',
  gender: userStore.userInfo.gender,
  birthday: userStore.userInfo.birthday
})

const saving = ref(false)
const showChangePassword = ref(false)
const showBindPhone = ref(false)
const showBindEmail = ref(false)
const changingPassword = ref(false)
const bindingPhone = ref(false)
const bindingEmail = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

// 表单数据
const passwordForm = reactive<PasswordForm>({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const phoneForm = reactive<PhoneForm>({
  newPhone: '',
  code: ''
})

const emailForm = reactive<EmailForm>({
  newEmail: '',
  code: ''
})

// 表单验证规则
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule: any, value: string, callback: any) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateEmail = (rule: any, value: string, callback: any) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符之间', trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const phoneRules = {
  newPhone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const emailRules = {
  newEmail: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 方法
const handleAvatarChange = (file: UploadFile) => {
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = (e) => {
      userInfo.value.avatar = e.target?.result as string
    }
    reader.readAsDataURL(file.raw)
  }
}

const saveProfile = async () => {
  try {
    saving.value = true
    // 这里应该调用API保存用户资料
    // await updateUserInfo(userInfo.value)
    userStore.setUserInfo(userInfo.value)
    ElMessage.success('资料保存成功')
  } catch (error) {
    console.error('保存资料失败:', error)
    ElMessage.error('保存资料失败')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  userInfo.value = {
    ...userStore.userInfo,
    gender: userStore.userInfo.gender,
    birthday: userStore.userInfo.birthday
  }
}

const changePassword = async () => {
  try {
    await passwordFormRef.value?.validate()
    changingPassword.value = true
    // 这里应该调用API修改密码
    // await changePasswordAPI(passwordForm)
    ElMessage.success('密码修改成功')
    showChangePassword.value = false
    resetPasswordForm()
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  } finally {
    changingPassword.value = false
  }
}

const bindPhone = async () => {
  try {
    await phoneFormRef.value?.validate()
    bindingPhone.value = true
    // 这里应该调用API绑定手机
    // await bindPhoneAPI(phoneForm)
    userInfo.value.phone = phoneForm.newPhone
    userStore.setUserInfo(userInfo.value)
    ElMessage.success('手机绑定成功')
    showBindPhone.value = false
    resetPhoneForm()
  } catch (error) {
    console.error('绑定手机失败:', error)
    ElMessage.error('绑定手机失败')
  } finally {
    bindingPhone.value = false
  }
}

const bindEmail = async () => {
  try {
    await emailFormRef.value?.validate()
    bindingEmail.value = true
    // 这里应该调用API绑定邮箱
    // await bindEmailAPI(emailForm)
    userInfo.value.email = emailForm.newEmail
    userStore.setUserInfo(userInfo.value)
    ElMessage.success('邮箱绑定成功')
    showBindEmail.value = false
    resetEmailForm()
  } catch (error) {
    console.error('绑定邮箱失败:', error)
    ElMessage.error('绑定邮箱失败')
  } finally {
    bindingEmail.value = false
  }
}

const sendCode = async (type: 'phone' | 'email') => {
  try {
    sendingCode.value = true
    
    // 验证输入
    if (type === 'phone' && !phoneForm.newPhone) {
      await phoneFormRef.value?.validateField('newPhone')
      return
    }
    if (type === 'email' && !emailForm.newEmail) {
      await emailFormRef.value?.validateField('newEmail')
      return
    }
    
    // 这里应该调用API发送验证码
    // await sendCodeAPI(type, type === 'phone' ? phoneForm.newPhone : emailForm.newEmail)
    
    ElMessage.success('验证码发送成功')
    
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败')
  } finally {
    sendingCode.value = false
  }
}

const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmNewPassword = ''
  passwordFormRef.value?.clearValidate()
}

const resetPhoneForm = () => {
  phoneForm.newPhone = ''
  phoneForm.code = ''
  phoneFormRef.value?.clearValidate()
}

const resetEmailForm = () => {
  emailForm.newEmail = ''
  emailForm.code = ''
  emailFormRef.value?.clearValidate()
}

const getRoleTagType = (role: string) => {
  return role === 'USER' ? 'primary' : role === 'DRIVER' ? 'success' : 'info'
}

const getRoleDisplay = (role: string) => {
  const roleMap: Record<string, string> = {
    'USER': '乘客',
    'DRIVER': '司机',
    'ADMIN': '管理员'
  }
  return roleMap[role] || role
}

onMounted(() => {
  // 初始化用户信息
  userInfo.value = {
    ...userStore.userInfo,
    gender: userStore.userInfo.gender,
    birthday: userStore.userInfo.birthday
  }
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.avatar-uploader {
  margin-right: 20px;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .avatar-uploader-icon {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  font-size: 28px;
  color: #8c939d;
  text-align: center;
}

.avatar-text h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
}

.security-content {
  padding: 10px 0;
}

.code-input {
  display: flex;
}

.profile-form {
  max-width: 600px;
}
</style>