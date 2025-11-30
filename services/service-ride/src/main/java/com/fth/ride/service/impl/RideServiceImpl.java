package com.fth.ride.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fth.ride.domain.dto.RideCreateRequest;
import com.fth.ride.domain.dto.RideResponse;
import com.fth.ride.domain.model.Ride;
import com.fth.ride.mapper.RideMapper;
import com.fth.ride.service.RideService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideMapper mapper;

    @Override
    public RideResponse createRide(RideCreateRequest req) {

        Ride ride = new Ride();

        ride.setRideId(UUID.randomUUID().toString());
        ride.setPassengerId(req.getPassengerId());

        // 经纬度存储（假设你已经改成 double 类型）
        ride.setOriginLat(req.getOriginLat());
        ride.setOriginLng(req.getOriginLng());
        ride.setOriginAddress(req.getOriginAddress());
        ride.setDestLat(req.getDestLat());
        ride.setDestLng(req.getDestLng());
        ride.setDestAddress(req.getDestAddress());

        // 基础状态初始化
        ride.setStatus("REQUESTED");
        ride.setRequestTime(LocalDateTime.now());

        mapper.insert(ride);

        return RideResponse.from(ride);
    }

    @Override
    public List<RideResponse> getHistoryRidesByPassengerId(String passengerId) {

        LambdaQueryWrapper<Ride> qw = new LambdaQueryWrapper<>();
        qw.eq(Ride::getPassengerId, passengerId)
                .orderByDesc(Ride::getRequestTime);

        List<Ride> rides = mapper.selectList(qw);

        return rides.stream()
                .map(RideResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public RideResponse updateRideStatus(String rideId,String status) {
        Ride ride = mapper.selectById(rideId);

        if (ride == null) return null;

        ride.setStatus(status);

        // 自动时间流转
        if (status == "PICKEDUP") {
            ride.setStartTime(LocalDateTime.now());
        }
        if (status == "FINISHED") {
            ride.setEndTime(LocalDateTime.now());
        }

        mapper.updateById(ride);

        return RideResponse.from(ride);
    }
}
