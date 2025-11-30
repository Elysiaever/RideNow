package com.fth.driver.domain.dto;

import lombok.Data;

@Data
public class SearchDriverDto {
    private Double lng;
    private Double lat;
    private Double radius;
}
