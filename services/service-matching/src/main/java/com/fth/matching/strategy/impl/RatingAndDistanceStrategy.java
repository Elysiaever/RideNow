package com.fth.matching.strategy.impl;

import com.fth.driver.domain.data.DriverWithDistance;
import com.fth.matching.strategy.DriverMatchingStrategy;
import com.fth.matching.util.DistanceUtils;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 综合评分和距离的匹配策略
 * 评分越高、距离越近，优先级越高
 */
@Component("ratingAndDistanceStrategy")
public class RatingAndDistanceStrategy implements DriverMatchingStrategy {

    // 权重可以根据业务需求调整
    private static final double RATING_WEIGHT = 0.6; // 评分权重
    private static final double DISTANCE_WEIGHT = 0.4; // 距离权重
    private static final double MAX_DISTANCE = 5000; // 最大考虑距离（米），超过此距离权重降为0
    private static final double MAX_RATING = 5.0; // 最高评分

    @Override
    public List<DriverWithDistance> match(List<DriverWithDistance> candidates, double passengerLng, double passengerLat) {
        return candidates.stream()
                .map(driver -> {
                    // 计算距离得分（距离越近得分越高，归一化到0-1）
                    double distance = DistanceUtils.calculateDistance(
                            passengerLng, passengerLat,
                            driver.getLng(), driver.getLat()
                    );
                    double distanceScore = distance > MAX_DISTANCE ? 0 : 1 - (distance / MAX_DISTANCE);

                    // 计算评分得分（归一化到0-1）
                    double ratingScore = driver.getRating() / MAX_RATING;

                    // 计算综合得分
                    double finalScore = (ratingScore * RATING_WEIGHT) + (distanceScore * DISTANCE_WEIGHT);
                    driver.setMatchingScore(finalScore); // 需要在Driver类中添加matchingScore字段
                    return driver;
                })
                .sorted(Comparator.comparingDouble(DriverWithDistance::getMatchingScore).reversed()) // 降序排序
                .collect(Collectors.toList());
    }
}