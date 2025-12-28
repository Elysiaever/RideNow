package com.fth.driver.domain.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 司机实体类
 * 对应业务中的司机信息，包含匹配策略所需的核心属性
 */
@Data // 生成getter、setter、toString、equals、hashCode等方法
@NoArgsConstructor // 生成无参构造器
@AllArgsConstructor // 生成全参构造器（可选，方便快速创建对象）
public class DriverWithDistance implements Serializable {

    /**
     * 序列化版本号（用于对象序列化/反序列化，保证版本一致性）
     */
    private static final long serialVersionUID = 1L;

    /**
     * 司机唯一标识ID（主键）
     */
    private Long id;

    /**
     * 司机姓名
     */
    private String name;

    /**
     * 司机当前位置经度
     * 备注：采用WGS84坐标系（主流地图接口通用，如高德、百度（需转换）、谷歌）
     */
    private double lng;

    /**
     * 司机当前位置纬度
     * 备注：采用WGS84坐标系
     */
    private double lat;

    /**
     * 司机综合评分
     * 业务约束：0.0 - 5.0分（0分为初始无评分，5分为满分）
     */
    private double rating;

    /**
     * 司机匹配得分（用于综合策略排序）
     * 业务说明：由匹配策略计算赋值，得分越高匹配优先级越高，初始值为0.0
     */
    private double matchingScore;
}