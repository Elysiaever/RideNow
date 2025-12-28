package com.fth.ride.service;

import com.fth.ride.domain.dto.RideResponse;
import com.fth.ride.domain.dto.RideCreateRequest;

import java.util.List;

/**
 * 行程服务接口
 */
public interface RideService {
    RideResponse createRide(RideCreateRequest RideCreateRequest ); // 创建新行程
    List<RideResponse> getHistoryRidesByPassengerId(String passengerId); // 查询历史行程
    RideResponse updateRideStatus(String rideId, String status); // 修改行程状态
}
