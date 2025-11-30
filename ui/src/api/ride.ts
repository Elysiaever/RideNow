// src/api/ride.ts
import request from '../utils/request'

// Ride 创建请求 DTO
export interface CreateRideDto {
  passengerId: string;
  origin: string;
  destination: string;
}

// Ride 响应
export interface Ride {
  rideId: string;
  driverId: string;
  passengerId: string;
  startTime: string;
  endTime: string;
  origin: string;
  destination: string;
  status: string;
}

// 创建行程
export function createRide(data: CreateRideDto) {
  return request.post<Ride>("/api/ride/create", data);
}

// 获取历史行程
export function getHistoryRidesByPassengerId(passengerId: string) {
  return request.get<Ride[]>(`/api/ride/history/${passengerId}`);
}

// 更新行程状态
export function updateRideStatus(rideId: string, status: string) {
  return request.post<Ride>("/api/ride/change-status", null, {
    params: { rideId, status }
  });
}
