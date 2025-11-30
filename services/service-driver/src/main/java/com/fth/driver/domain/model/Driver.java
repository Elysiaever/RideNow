package com.fth.driver.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_driver")
public class Driver {

    @TableId(type = IdType.AUTO)
    private Long id;                   // 主键 ID

    @TableField("user_id")            // 指定数据库字段名
    private Long userId;               // 外键：关联 t_user(id)

    private String status;             // AVAILABLE / BUSY / OFFLINE
    private Double rating;             // 司机评分（默认 5.0）

    @TableField("vehicle_brand")      // 指定数据库字段名
    private String vehicleBrand;       // 车辆品牌

    @TableField("vehicle_model")      // 指定数据库字段名
    private String vehicleModel;       // 车辆型号

    @TableField("vehicle_number")     // 指定数据库字段名
    private String vehicleNumber;      // 车牌号

    @TableField("created_at")         // 指定数据库字段名
    private LocalDateTime createdAt;   // 创建时间

    @TableField("updated_at")         // 指定数据库字段名
    private LocalDateTime updatedAt;   // 更新时间
}