// src/api/login.ts
import request from '../utils/request'

export function login(username: string, password: string, role: string) {
    return request.post('/api/user/login', {
        username,
        password,
        role
    })
}
