package com.fth.ride.dto;

import com.fth.ride.enums.RideStatus;
import lombok.Data;

/**
 * 更新行程状态
 */
@Data
public class UpdateRideStatusRequest {
    private String rideId;
    private RideStatus status;
    private String reason; // 若取消，可以填写取消原因
}