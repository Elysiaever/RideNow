package com.fth.ride.controller;

import com.fth.common.api.Response;
import com.fth.ride.domain.dto.RideResponse;
import com.fth.ride.domain.dto.RideCreateRequest;
import com.fth.ride.service.RideService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行程控制器
 */
@RestController
@RequestMapping("/api/ride")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService; // Spring 注入
    }

    /**
     * 创建新行程
     */
    @PostMapping("/create")
    public Response<RideResponse> createRide(@RequestBody RideCreateRequest rideCreateRequest) {
        RideResponse response = rideService.createRide(rideCreateRequest);
        return Response.success(response);
    }

    /**
     * 根据乘客 ID 查询历史行程
     */
    @GetMapping("/history/{passengerId}")
    public Response<List<RideResponse>> getHistoryRidesByPassengerId(@PathVariable String passengerId) {
        List<RideResponse> response = rideService.getHistoryRidesByPassengerId(passengerId);
        return Response.success(response);
    }

    /**
     * 修改行程状态
     */
    @PostMapping("/change-status")
    public Response<RideResponse> updateStatus(@RequestParam String rideId,
                                     @RequestParam String status) {
        RideResponse response = rideService.updateRideStatus(rideId, status);
        return Response.success(response);
    }

    /**
     * 获取司机附近的行程
     */
    @GetMapping("/nearby/{driverId}")
    public Response<List<RideResponse>> getNearbyRides(@PathVariable String driverId) {
        List<RideResponse> response = rideService.getNearbyRides(driverId);
        return Response.success(response);
    }

    /**
     * 获取行程详情
     */
    @GetMapping("/detail/{rideId}")
    public Response<RideResponse> getRideDetail(@PathVariable String rideId) {
        RideResponse response = rideService.getRideDetail(rideId);
        return Response.success(response);
    }

    /**
     * 司机接单
     */
    @PostMapping("/accept")
    public Response<Void> acceptRide(@RequestBody AcceptRideRequest request) {
        rideService.acceptRide(request.getDriverId(), request.getRideId());
        return Response.success(null);
    }

    /**
     * 司机到达乘客位置
     */
    @PostMapping("/arrived/{rideId}")
    public Response<Void> driverArrived(@PathVariable String rideId) {
        rideService.driverArrived(rideId);
        return Response.success(null);
    }

    /**
     * 开始行程
     */
    @PostMapping("/start/{rideId}")
    public Response<Void> startRide(@PathVariable String rideId) {
        rideService.startRide(rideId);
        return Response.success(null);
    }

    /**
     * 结束行程
     */
    @PostMapping("/end/{rideId}")
    public Response<Void> endRide(@PathVariable String rideId) {
        rideService.endRide(rideId);
        return Response.success(null);
    }

    // 内部类用于接收接单请求
    public static class AcceptRideRequest {
        private String driverId;
        private String rideId;

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getRideId() {
            return rideId;
        }

        public void setRideId(String rideId) {
            this.rideId = rideId;
        }
    }
}