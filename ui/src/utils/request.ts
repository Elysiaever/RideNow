// src/utils/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import router from '../router'

// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:80',
    timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const auth = useAuthStore()
        // 添加token
        if (auth.token) {
            config.headers.Authorization = `Bearer ${auth.token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        return response
    },
    (error) => {
        // 处理401未授权错误
        if (error.response && error.response.status === 401) {
            const auth = useAuthStore()
            auth.logout()
            router.push('/login')
        } else {
            ElMessage.error(error.message || '请求失败')
        }
        return Promise.reject(error)
    }
)

export default request