package com.fth.ride.domain.dto;

import lombok.Data;

/**
 * 创建行程请求 DTO
 */
@Data
public class RideCreateRequest {
    private String passengerId;
    private double originLat;
    private double originLng;
    private double destLat;
    private double destLng;
}