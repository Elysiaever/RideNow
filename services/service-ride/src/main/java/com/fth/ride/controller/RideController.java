package com.fth.ride.controller;

import com.fth.ride.domain.dto.RideResponse;
import com.fth.ride.domain.enums.RideStatus;
import com.fth.ride.domain.dto.RideCreateRequest;
import com.fth.ride.service.RideService;
import com.fth.ride.service.impl.RideServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行程控制器
 */
@RestController
@RequestMapping("api/ride")
public class RideController {

    private final RideService rideService = new RideServiceImpl();

    /**
     * 创建新行程
     */
    @PostMapping("/create")
    public RideResponse createRide(@RequestBody RideCreateRequest rideCreateRequest) {
        return rideService.createRide(rideCreateRequest);
    }

    /**
     * 根据乘客 ID 查询历史行程
     * GET /api/ride/history?passengerId=xxx
     */
    @GetMapping("/history/{passengerId}")
    public List<RideResponse> getHistoryRidesByPassengerId(@PathVariable String passengerId) {
        return rideService.getHistoryRidesByPassengerId(passengerId);
    }

    /**
     * 修改行程状态
     */
    @PostMapping("/change-status")
    public RideResponse updateStatus(@RequestParam String rideId,
                                     @RequestParam RideStatus status) {
        return rideService.updateRideStatus(rideId, status);
    }


}