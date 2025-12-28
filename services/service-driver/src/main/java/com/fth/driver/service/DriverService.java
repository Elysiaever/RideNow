package com.fth.driver.service;

import com.fth.driver.domain.data.DriverWithDistance;
import com.fth.driver.domain.model.Driver;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DriverService {

    void updateLocation(Long driverId, Double lng, Double lat);
    List<?> searchNearby(Double lng, Double lat, Double radius);

    //增加司机
    void addDriver(Driver driver);
    //删除司机
    void deleteDriver(Long driverId);
    //修改司机
    void updateDriver(Driver driver);
    //查询司机
    Driver getDriverById(Long driverId);

    /**
     * 获取所有在线且可接单的司机列表（包含完整经纬度、评分等信息）
     * @return 符合条件的司机列表
     */
    List<DriverWithDistance> getAllOnlineAndAvailableDrivers();

    void driverOnline(Long driverId,double lng,double lat);

    void driverOffline(Long driverId);

    boolean isDriverOnline(Long driverId);
}
