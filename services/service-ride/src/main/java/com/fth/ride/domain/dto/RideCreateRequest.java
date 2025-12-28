package com.fth.ride.domain.dto;

import lombok.Data;

/**
 * 创建行程请求 DTO
 */
@Data
public class RideCreateRequest {
    private String passengerId;
    private String originAddress;
    private double originLat;
    private double originLng;
    private String destAddress;
    private double destLat;
    private double destLng;
}