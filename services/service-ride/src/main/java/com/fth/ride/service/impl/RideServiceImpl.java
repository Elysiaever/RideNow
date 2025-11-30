package com.fth.ride.service.impl;

import com.fth.ride.dto.RideCreateRequest;
import com.fth.ride.dto.RideResponse;
import com.fth.ride.enums.RideStatus;
import com.fth.ride.model.Ride;
import com.fth.ride.mapper.RideMapper;
import com.fth.ride.service.RideService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 行程服务实现类（使用内存存储）
 */
public class RideServiceImpl implements RideService {

    private final RideMapper mapper = new RideMapper();

    @Override
    public RideResponse createRide(RideCreateRequest rideCreateRequest) {
        Ride ride = new Ride();

        ride.setPassengerId(rideCreateRequest.getPassengerId());
        ride.setOrigin(rideCreateRequest.getOrigin());
        ride.setDestination(rideCreateRequest.getDestination());

        // 自动生成 rideId
        if (ride.getRideId() == null || ride.getRideId().isEmpty()) {
            ride.setRideId(UUID.randomUUID().toString());
        }

        // 初始化状态和时间
        ride.setStatus(RideStatus.REQUESTED);
        if (ride.getRequestTime() == null) {
            ride.setRequestTime(java.time.LocalDateTime.now());
        }

        // 保存行程
        mapper.insert(ride);

        return RideResponse.from(ride);
    }

    @Override
    public List<RideResponse> getHistoryRidesByPassengerId(String passengerId) {
        List<Ride> rides = mapper.selectByPassengerId(passengerId);
        return rides.stream()
                .map(RideResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public RideResponse updateRideStatus(String rideId, RideStatus status) {
        Ride ride = mapper.selectById(rideId);
        if (ride != null) {
            ride.setStatus(status);

            // 如果状态是开始/结束，自动设置时间
            if (status == RideStatus.PICKEDUP) ride.setStartTime(java.time.LocalDateTime.now());
            if (status == RideStatus.FINISHED) ride.setEndTime(java.time.LocalDateTime.now());

            mapper.update(ride);
            return RideResponse.from(ride);
        }
        return null;
    }
}