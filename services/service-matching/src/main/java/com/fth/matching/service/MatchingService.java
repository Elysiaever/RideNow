package com.fth.matching.service;

import com.fth.driver.domain.data.DriverWithDistance;

import java.util.List;

public interface MatchingService {

    //找到最匹配的几个司机
    List<DriverWithDistance> findBestMatchingDrivers(double lng, double lat);

    List<DriverWithDistance> getAllDrivers();
}
