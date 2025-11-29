package com.fth.ride.enums;

public enum RideStatus {
    REQUESTED,     // 乘客提交请求待派单
    DISPATCHING,   // 派单中
    ACCEPTED,      // 司机已接单
    ARRIVED,       // 司机到达上车点
    PICKEDUP,      // 乘客已上车
    ONGOING,       // 行程中
    FINISHED,      // 行程结束
    CANCELLED      // 行程取消
}
