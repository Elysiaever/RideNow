// src/stores/userStore.ts
import { defineStore } from 'pinia'
import { useAuthStore } from './auth'

interface UserInfo {
    id?: number
    username?: string
    nickname?: string
    phone?: string
    email?: string
    roles?: string[]
    avatar?: string
    gender?: string
    birthday?: string
}

export const useUserStore = defineStore('userStore', {
    state: () => ({
        // 现在主要依赖auth store中的用户信息
    }),

    getters: {
        userInfo(): UserInfo {
            const authStore = useAuthStore()
            return authStore.userInfo
        },
        
        isLoggedIn(): boolean {
            const authStore = useAuthStore()
            return authStore.isLoggedIn
        }
    },

    actions: {
        // 通过auth store来管理用户信息
        setUserInfo(info: UserInfo) {
            const authStore = useAuthStore()
            authStore.setUserInfo(info)
        },

        clearUserInfo() {
            const authStore = useAuthStore()
            authStore.userInfo = {}
        },
        
        loadStoredUserInfo() {
            const authStore = useAuthStore()
            authStore.loadStoredUserInfo()
        }
    }
})
