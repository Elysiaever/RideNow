import { defineStore } from 'pinia'
// 导入 request.ts 中的自定义 axios 实例，而非全局 axios
import request from '../utils/request'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null
    }),

    getters: {
        isLoggedIn: (state) => !!state.token
    },

    actions: {
        setAuth(token: string) {
            this.token = token
            localStorage.setItem('token', token)
            // 改为给自定义 request 实例设置默认 headers
            request.defaults.headers.common['Authorization'] = `Bearer ${token}`
        },

        logout() {
            this.token = null
            localStorage.removeItem('token')
            // 同时清除自定义实例的 Authorization 头
            delete request.defaults.headers.common['Authorization']
        },

        // 判断 token 是否过期（保持不变）
        isTokenExpired(): boolean {
            if (!this.token) return true
            try {
                const payloadBase64 = this.token.split('.')[1]
                if (!payloadBase64) return true
                const payload = JSON.parse(atob(payloadBase64))
                return !payload.exp || Math.floor(Date.now() / 1000) >= payload.exp
            } catch {
                return true
            }
        }
    }
})