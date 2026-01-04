// src/api/ride.ts
import request from '../utils/request'

// Ride 创建请求 DTO
export interface CreateRideDto {
  passengerId: string;
  originAddress: string;
  destAddress: string;
  originLng: number;
  originLat: number;
  destLng: number;
  destLat: number;
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
  originLng: number;
  originLat: number;
  destLng: number;
  destLat: number;
  status: string;
  fare?: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// 创建行程
export function createRide(data: CreateRideDto) {
  return request.post<ApiResponse<Ride>>("/api/ride/create", data);
}

// 获取历史行程
export function getHistoryRidesByPassengerId(passengerId: string) {
  return request.get<ApiResponse<Ride[]>>(`/api/ride/history/${passengerId}`);
}

// 更新行程状态
export function updateRideStatus(rideId: string, status: string) {
  return request.post<ApiResponse<Ride>>("/api/ride/change-status", null, {
    params: { rideId, status }
  });
}

// 获取司机附近的行程
export function getNearbyRides(driverId: string) {
  return request.get<ApiResponse<Ride[]>>(`/api/ride/nearby/${driverId}`);
}

// 获取行程详情
export function getRideDetail(rideId: string) {
  return request.get<ApiResponse<Ride>>(`/api/ride/detail/${rideId}`);
}

// 司机接单
export function acceptRide(driverId: string, rideId: string) {
  return request.post<ApiResponse<null>>(`/api/ride/accept`, { driverId, rideId });
}

// 司机到达乘客位置
export function driverArrived(rideId: string) {
  return request.post<ApiResponse<null>>(`/api/ride/arrived/${rideId}`);
}

// 开始行程
export function startRide(rideId: string) {
  return request.post<ApiResponse<null>>(`/api/ride/start/${rideId}`);
}

// 结束行程
export function endRide(rideId: string) {
  return request.post<ApiResponse<null>>(`/api/ride/end/${rideId}`);
}
