// src/api/driver.ts
import request from '../utils/request'

// 司机上线
// 后端接口: PUT /api/driver/online
// 参数: Body { driverId, lng, lat }
export function driverOnline(data: { driverId: number; lng: number; lat: number }) {
    return request.put('/api/driver/online', data)
}

// 司机下线
// 后端接口: PUT /api/driver/offline
// 参数: Query ?driverId=xx
export function driverOffline(driverId: number) {
    // 写法 A: 如果你的 request 封装支持 axios 标准的第三个参数
    return request.put('/api/driver/offline', null, {
        params: { driverId }
    })
    
    // 写法 B: 如果写法 A 报错，可以用这种最稳妥的拼串方式：
    // return request.put(`/api/driver/offline?driverId=${driverId}`)
}