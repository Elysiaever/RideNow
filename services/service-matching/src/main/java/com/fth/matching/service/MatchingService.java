package com.fth.matching.service;

import java.util.List;

public interface MatchingService {

    //找到最匹配的几个司机
    List<?> findBestMatchingDrivers(double lng, double lat);
}
