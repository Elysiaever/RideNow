package com.fth.driver.mq.consumer;

import com.fth.common.core.constant.RedisConstant;
import com.fth.driver.constant.RabbitMqConstant;
import com.fth.driver.domain.dto.mq.DriverLocationMqMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate; // 替换为 StringRedisTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ消费端 - 司机位置更新（修复RedisTemplate注入问题）
 */
@Slf4j
@Component
public class DriverLocationConsumer {

    // 1. 替换为 StringRedisTemplate（Spring Boot自动配置，直接可用，无需额外定义）
    // 备注：StringRedisTemplate 是 RedisTemplate<String, String> 的子类，专门用于字符串操作，完美适配Redis GEO
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 核心消费方法：监听队列，异步消费消息并写入Redis GEO
     */
    @RabbitListener(queues = RabbitMqConstant.DRIVER_LOCATION_QUEUE)
    public void consumeDriverLocation(DriverLocationMqMsg locationMqMsg) {
        try {
            // 2. 提取消息数据
            Long driverId = locationMqMsg.getDriverId();
            Double lng = locationMqMsg.getLng();
            Double lat = locationMqMsg.getLat();

            log.info("╔════════════════════════════════════════╗");
            log.info("║        异步消费消息（开始写入Redis GEO） ║");
            log.info("╚════════════════════════════════════════╝");
            log.info("从队列取出司机ID: {}", driverId);
            log.info("从队列取出经度: {}, 纬度: {}", lng, lat);
            log.info("Redis GEO Key: {}", RedisConstant.DRIVER_GEO_KEY);

            // 3. 写入Redis GEO（StringRedisTemplate 用法与原 RedisTemplate 一致，兼容GEO操作）
            Long count = stringRedisTemplate.opsForGeo().add(
                    RedisConstant.DRIVER_GEO_KEY,
                    new Point(lng, lat),
                    driverId.toString()
            );

            // 4. 日志记录写入结果
            log.info("Redis GEO写入结果: {} (1=新增司机位置, 0=更新已有司机位置)", count);
            log.info("════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ 异步消费消息失败（写入Redis GEO异常）: {}", e.getMessage(), e);
        }
    }
}