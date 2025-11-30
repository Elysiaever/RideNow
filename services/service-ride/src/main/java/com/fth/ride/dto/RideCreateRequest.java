package com.fth.ride.dto;

import lombok.Data;

/**
 * 创建行程请求 DTO
 */
@Data
public class RideCreateRequest {
    private String passengerId;
    private String origin;
    private String destination;
}