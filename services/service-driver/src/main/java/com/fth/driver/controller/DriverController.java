package com.fth.driver.controller;

import com.fth.driver.domain.dto.LocationUpdateDto;
import com.fth.driver.domain.dto.SearchDriverDto;
import com.fth.driver.domain.model.Driver;
import com.fth.driver.service.DriverService;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PutMapping("/updateLocation")
    public String updateLocation(@RequestBody LocationUpdateDto locationUpdateDto) {
        driverService.updateLocation(locationUpdateDto.getDriverId(), locationUpdateDto.getLng(), locationUpdateDto.getLat());
        return "Location updated";
    }

    @GetMapping("/searchNearby")
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
