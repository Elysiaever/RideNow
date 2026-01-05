package com.fth.driver.filter;

/**
 * 司机位置过滤器接口
 * 用于判断是否应该更新司机位置
 */
public interface DriverLocationFilter {

    /**
     * 判断是否应该忽略本次位置更新
     *
     * @param distance 与上次位置的距离（米）
     * @param timeDiff 与上次更新的时间差（毫秒）
     * @return true=忽略更新, false=允许更新
     */
    boolean shouldIgnoreUpdate(double distance, long timeDiff);
}
