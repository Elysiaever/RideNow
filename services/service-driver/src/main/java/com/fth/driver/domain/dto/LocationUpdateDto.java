package com.fth.driver.domain.dto;

import lombok.Data;

@Data
public class LocationUpdateDto {
    private Long driverId;
    private Double lng;
    private Double lat;
}
