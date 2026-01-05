package com.fth.driver.filter.impl;

import com.fth.driver.filter.DriverLocationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 时间+距离双重阈值过滤器实现
 * 当距离小于阈值且时间间隔小于阈值时，忽略本次更新
 */
@Slf4j
@Component
public class TimeAndDistanceFilter implements DriverLocationFilter {

    /**
     * 距离阈值（米），从配置文件读取
     */
    @Value("${driver.location.filter.distance-threshold:20}")
    private double distanceThreshold;

    /**
     * 时间阈值（毫秒），从配置文件读取
     */
    @Value("${driver.location.filter.time-threshold:10000}")
    private long timeThreshold;

    @Override
    public boolean shouldIgnoreUpdate(double distance, long timeDiff) {
        boolean shouldIgnore = distance < distanceThreshold && timeDiff <= timeThreshold;
        
        if (shouldIgnore) {
            log.info("位置更新被忽略 - 距离: {} 米 < {} 米, 时间间隔: {} 毫秒 <= {} 毫秒",
                    distance, distanceThreshold, timeDiff, timeThreshold);
        } else {
            log.info("允许位置更新 - 距离: {} 米, 时间间隔: {} 毫秒",
                    distance, timeDiff);
        }
        
        return shouldIgnore;
    }

    // 提供getter方法，方便日志和调试
    public double getDistanceThreshold() {
        return distanceThreshold;
    }

    public long getTimeThreshold() {
        return timeThreshold;
    }
}
