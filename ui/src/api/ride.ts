// src/api/ride.ts
import axios from "axios";

const http = axios.create({
  baseURL: "http://localhost:80",
  timeout: 5000,
});

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
  return http.post<Ride>("/api/ride/create", data);
}

export function getHistoryRidesByPassengerId(passengerId: string) {
  return http.get<Ride[]>("/api/ride/history/" + passengerId);
}

export function updateRideStatus(rideId: string, status: string) {
  return http.post<Ride>("/api/ride/change-status", null, {
    params: { rideId, status }
  });
}