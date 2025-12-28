package com.fth.driver.controller;

import com.fth.common.core.constant.RedisConstant;
import com.fth.driver.domain.dto.LocationUpdateDto;
import com.fth.driver.domain.dto.SearchDriverDto;
import com.fth.driver.domain.model.Driver;
import com.fth.driver.service.DriverService;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 【测试用】添加几个测试司机到 Redis
     * 访问这个接口就能添加测试数据
     */
    @GetMapping("/test/init")
    public String initTestData() {
        // 清空旧数据
        redisTemplate.delete(RedisConstant.DRIVER_GEO_KEY);

        // 添加4个测试司机（北京天安门附近）
        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.404, 39.915),  // 天安门
                "driver001"
        );

        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.408, 39.918),  // 附近500米
                "driver002"
        );

        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.400, 39.910),  // 附近800米
                "driver003"
        );

        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.420, 39.920),  // 附近2公里
                "driver004"
        );

        return "✅ 成功添加4个测试司机！";
    }

    @Autowired
    private DriverService driverService;

    @PutMapping("/updateLocation")
    public String updateLocation(@RequestBody LocationUpdateDto locationUpdateDto) {
        driverService.updateLocation(locationUpdateDto.getDriverId(), locationUpdateDto.getLng(), locationUpdateDto.getLat());
        return "Location updated";
    }

    @PutMapping("/searchNearby")
    public List<?> searchNearby(@RequestBody SearchDriverDto searchDriverDto) {

        return driverService.searchNearby(searchDriverDto.getLng(),searchDriverDto.getLat(),searchDriverDto.getRadius());
    }

    //增加司机
    @PutMapping("/add")
    public String addDriver(@RequestBody Driver driver) {
        driverService.addDriver(driver);
        return "Driver added";
    }

    //删除司机
    @PutMapping("/delete")
    public String deleteDriver(Long driverId) {
        driverService.deleteDriver(driverId);
        return "Driver deleted";
    }

    //修改司机信息
    @PutMapping("/update")
    public String updateDriver(@RequestBody Driver driver) {
        driverService.updateDriver(driver);
        return "Driver updated";
    }

    //查询司机信息

    @GetMapping("/get")
    public Driver getDriver(Long driverId) {
        return driverService.getDriverById(driverId);
    }

    @PutMapping("/online")
    public String driverOnline(@RequestBody LocationUpdateDto locationUpdateDto) {
        driverService.driverOnline(locationUpdateDto.getDriverId(), locationUpdateDto.getLng(), locationUpdateDto.getLat());
        return "Driver online";
    }

    @PutMapping("/offline")
    public String driverOffline(Long driverId) {
        driverService.driverOffline(driverId);
        return "Driver offline";
    }
}
