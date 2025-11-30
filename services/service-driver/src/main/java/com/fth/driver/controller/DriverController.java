package com.fth.driver.controller;

import com.fth.driver.domain.dto.LocationUpdateDto;
import com.fth.driver.domain.dto.SearchDriverDto;
import com.fth.driver.domain.model.Driver;
import com.fth.driver.service.DriverService;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Driver Service";
    }

    @PutMapping("/updateLocation")
    public String updateLocation(LocationUpdateDto locationUpdateDto) {
        driverService.updateLocation(locationUpdateDto.getDriverId(), locationUpdateDto.getLng(), locationUpdateDto.getLat());
        return "Location updated";
    }

    @GetMapping("/searchNearby")
    public List<?> searchNearby(SearchDriverDto searchDriverDto) {

        return driverService.searchNearby(searchDriverDto.getLng(),searchDriverDto.getLat(),searchDriverDto.getRadius());
    }

    //增加司机
    @PutMapping("/add")
    public String addDriver(Driver driver) {
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
    public String updateDriver(Driver driver) {
        driverService.updateDriver(driver);
        return "Driver updated";
    }

    //查询司机信息

    @GetMapping("/get")
    public Driver getDriver(Long driverId) {
        return driverService.getDriverById(driverId);
    }

}
