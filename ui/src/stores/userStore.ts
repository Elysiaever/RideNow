// src/stores/userStore.ts
import { defineStore } from 'pinia'

interface UserInfo {
    id?: number
    username?: string
    nickname?: string
    phone?: string
    email?: string
    roles?: string[]
    avatar?: string
    // ... 根据你的后端信息继续补充
}

export const useUserStore = defineStore('userStore', {
    state: () => ({
        userInfo: {} as UserInfo,
    }),

    actions: {
        setUserInfo(info: UserInfo) {
            this.userInfo = info
        },

        clearUserInfo() {
            this.userInfo = {}
        }
    }
})
