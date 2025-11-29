package com.fth.ride.model;

import java.time.LocalDateTime;
import com.fth.ride.enums.RideStatus;
import lombok.Data;

@Data
public class Ride {

    // ===== 基础信息 =====
    private String rideId;
    private String passengerId;
    private String driverId;

    // ===== 位置描述 =====
    private String origin;
    private String destination;

    // ===== 多阶段时间 =====
    private LocalDateTime requestTime; // 乘客发起请求时间
    private LocalDateTime acceptTime;  // 司机接单时间
    private LocalDateTime startTime;   // 乘客上车，开始计费
    private LocalDateTime endTime;     // 行程结束时间

    // ===== 状态 =====
    private RideStatus status;

    // ===== 计费/统计数据 =====
    private double distance;  // 行程里程（km）
    private double duration;  // 行程时长（分钟）
    private double price;     // 最终价格

    // ===== 取消原因（用于记录取消状态） =====
    private String cancelReason;

    public Ride(String rideId,
                String passengerId,
                String origin,
                String destination) {

        this.rideId = rideId;
        this.passengerId = passengerId;
        this.origin = origin;
        this.destination = destination;

        this.status = RideStatus.REQUESTED;
        this.requestTime = LocalDateTime.now();
    }

    public Ride() {}

}