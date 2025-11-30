package com.fth.ride.domain.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_ride")
public class Ride {

    // ===== 基础信息 =====
    private String rideId;
    private String passengerId;
    private String driverId;

    // ===== 位置描述 =====
    private double originLat;
    private double originLng;
    private String originAddress;
    private double destLat;
    private double destLng;
    private String destAddress;

    // ===== 多阶段时间 =====
    private LocalDateTime requestTime; // 乘客发起请求时间
    private LocalDateTime acceptTime;  // 司机接单时间
    private LocalDateTime startTime;   // 乘客上车，开始计费
    private LocalDateTime endTime;     // 行程结束时间

    // ===== 状态 =====
    private String status;

    // ===== 计费/统计数据 =====
    private double distance;  // 行程里程（km）
    private double duration;  // 行程时长（分钟）
    private double price;     // 最终价格

    // ===== 取消原因（用于记录取消状态） =====
    private String cancelReason;

    public Ride(String rideId,
                String passengerId,
                double originLat,
                double originLng,
                double destLat,
                double destLng) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destLat = destLat;
        this.destLng = destLng;
        this.status = "REQUESTED";
        this.requestTime = LocalDateTime.now();
    }

    public Ride() {}

}