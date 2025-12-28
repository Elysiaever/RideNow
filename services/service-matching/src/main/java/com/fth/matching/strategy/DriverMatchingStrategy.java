package com.fth.matching.strategy;

import com.fth.driver.domain.data.DriverWithDistance;
import java.util.List;

/**
 * 司机匹配策略接口
 */
public interface DriverMatchingStrategy {

    /**
     * 根据策略匹配最佳司机
     * @param candidates 候选司机列表（已过滤一定距离内的司机）
     * @param passengerLng 乘客经度
     * @param passengerLat 乘客纬度
     * @return 匹配后的司机列表（按匹配度排序）
     */
    List<DriverWithDistance> match(List<DriverWithDistance> candidates, double passengerLng, double passengerLat);
}