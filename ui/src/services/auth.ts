import api from './api'

export function login(username: string, password: string, role: string) {
    return api.post('/api/user/login', {  // 新增 /api/user 前缀适配网关路由
        username,
        password,
        role
    })
}