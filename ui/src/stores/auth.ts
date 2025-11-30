import { defineStore } from 'pinia'
import axios from 'axios'

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
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
        },

        logout() {
            this.token = null
            localStorage.removeItem('token')
            delete axios.defaults.headers.common['Authorization']
        },

        // 判断 token 是否过期
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
