package com.fth.ride.domain.dto;

import lombok.Data;

/**
 * 更新行程状态
 */
@Data
public class UpdateRideStatusRequest {
    private String rideId;
    private String status;
    private String reason; // 若取消，可以填写取消原因
}