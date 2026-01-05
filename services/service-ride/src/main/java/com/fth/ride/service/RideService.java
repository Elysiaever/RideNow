package com.fth.ride.service;

import com.fth.ride.domain.dto.RideResponse;
import com.fth.ride.domain.dto.RideCreateRequest;

import java.util.List;

/**
 * 行程服务接口
 */
public interface RideService {
    RideResponse createRide(RideCreateRequest rideCreateRequest); // 创建新行程
    List<RideResponse> getHistoryRidesByPassengerId(String passengerId); // 查询历史行程
    RideResponse updateRideStatus(String rideId, String status); // 修改行程状态
    
    // 新增方法
    List<RideResponse> getNearbyRides(String driverId); // 获取司机附近的行程
    RideResponse getRideDetail(String rideId); // 获取行程详情
    void acceptRide(String driverId, String rideId); // 司机接单
    void driverArrived(String rideId); // 司机到达乘客位置
    void startRide(String rideId); // 开始行程
    void endRide(String rideId); // 结束行程
}
