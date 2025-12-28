// src/api/user.ts
import request from '../utils/request'

export function login(username: string, password: string) {
    return request.post('/api/user/login', {
        username,
        password
    })
}

// 添加注册接口
export function register(username: string, password: string) {
    return request.post('/api/user/register', {
        username,
        password
    })
}

// 通过用户名获取完整用户信息

export function getUserByUsername(username: string) {
    return request.get(`/api/user/getUserByUsername?username=${username}`)
}
