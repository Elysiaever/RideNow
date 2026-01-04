// src/api/user.ts
import request from '../utils/request'

export interface LoginRequest {
    username: string
    password: string
}

export interface LoginResponse {
    success: boolean
    message: string
    data: {
        token: string
        userId: string
        role: string
    }
}

export interface RegisterRequest {
    username: string
    password: string
}

export interface UserInfo {
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

export interface ApiResponse<T> {
    success: boolean
    message: string
    data: T
}

export function login(username: string, password: string) {
    const loginData: LoginRequest = { username, password };
    return request.post<ApiResponse<LoginResponse['data']>>('/api/user/login', loginData);
}

// 添加注册接口
export function register(username: string, password: string) {
    const registerData: RegisterRequest = { username, password };
    return request.post<ApiResponse<null>>('/api/user/register', registerData);
}

// 通过用户ID获取完整用户信息
export function getInfo(userId: string) {
    return request.get<ApiResponse<UserInfo>>(`/api/user/info?userId=${userId}`);
}

// 更新用户信息
export function updateUserInfo(userId: string, userInfo: Partial<UserInfo>) {
    return request.post<ApiResponse<null>>(`/api/user/update/${userId}`, userInfo);
}

// 修改密码
export function changePassword(userId: string, oldPassword: string, newPassword: string) {
    return request.post<ApiResponse<null>>(`/api/user/change-password/${userId}`, {
        oldPassword,
        newPassword
    });
}
