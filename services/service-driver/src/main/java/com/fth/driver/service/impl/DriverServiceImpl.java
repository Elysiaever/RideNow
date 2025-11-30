package com.fth.driver.service.impl;

import com.fth.driver.domain.model.Driver;
import com.fth.driver.mapper.DriverMapper;
import com.fth.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fth.common.service.RedisService;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private DriverMapper driverMapper;

    @Override
    public void updateLocation(Long driverId, Double lng, Double lat) {
        redisService.updateDriverLocation(driverId, lng, lat);
    }

    @Override
    public List<?> searchNearby(Double lng, Double lat, Double radius){
        return redisService.searchDriverNearby(lng, lat, radius);
    }

    @Override
    public void addDriver(Driver driver) {
        driverMapper.insert(driver);
    }

    @Override
    public void deleteDriver(Long driverId){
        driverMapper.deleteById(driverId);
    }

    @Override
    public void updateDriver(Driver driver){
        driverMapper.updateById(driver);
    }

    @Override
    public Driver getDriverById(Long driverId){
        return driverMapper.selectById(driverId);
    }
}
