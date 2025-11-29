package com.fth.ride.service;

import com.fth.ride.dto.RideResponse;
import com.fth.ride.model.Ride;
import com.fth.ride.dto.RideCreateRequest;
import com.fth.ride.enums.RideStatus;
import com.fth.ride.mapper.RideMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 行程服务接口
 */
public interface RideService {
    RideResponse createRide(RideCreateRequest RideCreateRequest ); // 创建新行程
    List<RideResponse> getHistoryRidesByPassengerId(String passengerId); // 查询历史行程
    RideResponse updateRideStatus(String rideId, RideStatus status); // 修改行程状态
}
