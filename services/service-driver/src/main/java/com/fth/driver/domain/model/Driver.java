package com.fth.driver.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_driver")
public class Driver {

    @TableId(type = IdType.AUTO)
    private Long id;                   // 主键 ID

    private Long userId;               // 外键：关联 t_user(id)

    private String status;             // AVAILABLE / BUSY / OFFLINE
    private Double rating;             // 司机评分（默认 5.0）

    private String vehicleBrand;       // 车辆品牌
    private String vehicleModel;       // 车辆型号
    private String vehicleNumber;      // 车牌号

    private LocalDateTime createdAt;   // 创建时间
    private LocalDateTime updatedAt;   // 更新时间
}