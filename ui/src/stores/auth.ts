import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token'),
        roles: localStorage.getItem('roles')
            ? JSON.parse(localStorage.getItem('roles')!)
            : []
    }),

    getters: {
        isLoggedIn: (state) => !!state.token
    },

    actions: {
        setAuth(data: { token: string; roles: string[] }) {
            this.token = data.token
            this.roles = data.roles

            localStorage.setItem('token', data.token)
            localStorage.setItem('roles', JSON.stringify(data.roles))

            axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`
        },

        logout() {
            this.token = null
            this.roles = []

            localStorage.removeItem('token')
            localStorage.removeItem('roles')

            delete axios.defaults.headers.common['Authorization']
        }
    }
})
