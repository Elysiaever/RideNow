package com.fth.driver.domain.dto.mq;

import lombok.Data;
import java.io.Serializable;

/**
 * 司机位置更新消息体 - 专用于RabbitMQ传输
 * 实现Serializable接口：保证对象在网络传输（RabbitMQ）中的序列化/反序列化正常
 */
@Data
public class DriverLocationMqMsg implements Serializable {
    // 序列化版本号，固定1L即可，避免序列化/反序列化版本冲突
    private static final long serialVersionUID = 1L;

    private Long driverId;  // 司机ID（与LocationUpdateDto对应）
    private Double lng;     // 经度（与LocationUpdateDto对应）
    private Double lat;     // 纬度（与LocationUpdateDto对应）
}