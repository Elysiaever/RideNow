package com.fth.matching.service.impl;

import com.fth.driver.domain.data.DriverWithDistance;
import com.fth.driver.service.DriverService;
import com.fth.matching.service.MatchingService;
import com.fth.matching.strategy.DriverMatchingStrategy;
import com.fth.matching.util.DistanceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingServiceImpl implements MatchingService {


    // 注入两种匹配策略
    @Autowired
    @Qualifier("nearestDistanceStrategy")
    private DriverMatchingStrategy nearestStrategy;

    @Autowired
    @Qualifier("ratingAndDistanceStrategy")
    private DriverMatchingStrategy ratingAndDistanceStrategy;

    // 核心：注入模拟的DriverService（依赖其获取司机数据）
    @Autowired
    private DriverService driverService;

    // 过滤司机的最大距离（可配置在application.yml中）
    private static final double FILTER_DISTANCE = 3000; // 3公里内的司机作为候选

    @Override
    public List<DriverWithDistance> findBestMatchingDrivers(double passengerLng, double passengerLat) {
        // 1. 调用getAllDrivers()获取所有在线可接单司机（数据来自DriverService）
        List<DriverWithDistance> allDrivers = getAllDrivers();

        // 2. 利用乘客经纬度（来自前端）过滤候选司机（3公里内）
        List<DriverWithDistance> candidates = allDrivers.stream()
                .filter(driver -> DistanceUtils.calculateDistance(
                        passengerLng, passengerLat,
                        driver.getLng(), driver.getLat()
                ) <= FILTER_DISTANCE)
                .collect(Collectors.toList());

        // 3. 应用匹配策略（可动态切换）
        // return nearestStrategy.match(candidates, passengerLng, passengerLat);
        return ratingAndDistanceStrategy.match(candidates, passengerLng, passengerLat);
    }

    /**
     * 实现getAllDrivers()：通过调用DriverService获取司机数据
     * 职责单一：仅获取全量在线可接单司机，不处理过滤/匹配逻辑
     * @return 全量在线可接单司机列表（包含完整经纬度、评分等信息）
     */
    @Override
    public List<DriverWithDistance> getAllDrivers() {
        // 直接调用模拟的DriverService方法，获取司机数据
        // 真实场景中，此处无需修改，仅需替换DriverService的实现即可
        return driverService.getAllOnlineAndAvailableDrivers();
    }
}
