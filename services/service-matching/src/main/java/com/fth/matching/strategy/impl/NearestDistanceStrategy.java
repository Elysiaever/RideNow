package com.fth.matching.strategy.impl;

import com.fth.driver.domain.data.DriverWithDistance;
import com.fth.matching.strategy.DriverMatchingStrategy;
import com.fth.matching.util.DistanceUtils;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 最近距离匹配策略
 */
@Component("nearestDistanceStrategy")
public class NearestDistanceStrategy implements DriverMatchingStrategy {

    @Override
    public List<DriverWithDistance> match(List<DriverWithDistance> candidates, double passengerLng, double passengerLat) {
        // 按距离升序排序（最近的在前）
        return candidates.stream()
                .sorted(Comparator.comparingDouble(driver -> 
                    DistanceUtils.calculateDistance(
                        passengerLng, passengerLat,
                        driver.getLng(), driver.getLat()
                    )
                ))
                .collect(Collectors.toList());
    }
}