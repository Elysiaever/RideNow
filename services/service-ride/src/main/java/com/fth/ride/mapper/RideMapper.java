package com.fth.ride.mapper;

import com.fth.ride.model.Ride;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存模拟 Ride 数据存储
 */
public class RideMapper {

    // 使用线程安全的 Map 存储行程数据
    private final Map<String, Ride> rideStore = new ConcurrentHashMap<>();

    /**
     * 插入新的行程
     */
    public void insert(Ride ride) {
        rideStore.put(ride.getRideId(), ride);
    }

    /**
     * 根据行程 ID 查询
     */
    public Ride selectById(String rideId) {
        return rideStore.get(rideId);
    }

    /**
     * 根据乘客 ID 查询历史行程列表
     */
    public List<Ride> selectByPassengerId(String passengerId) {
        return rideStore.values()
                .stream()
                .filter(r -> passengerId.equals(r.getPassengerId()))
                .sorted(Comparator.comparing(Ride::getRequestTime).reversed()) // 最新行程排前
                .collect(Collectors.toList());
    }

    /**
     * 更新行程状态（直接替换对象）
     */
    public void update(Ride ride) {
        rideStore.put(ride.getRideId(), ride);
    }

    /**
     * 查询所有行程（可选）
     */
    public List<Ride> selectAll() {
        return new ArrayList<>(rideStore.values());
    }
}
