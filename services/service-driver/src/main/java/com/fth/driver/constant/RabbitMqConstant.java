package com.fth.driver.constant;

public class RabbitMqConstant {
    // 1. 直连交换机（精准路由，适合位置更新这类一对一异步处理场景）
    public static final String DRIVER_LOCATION_EXCHANGE = "driver.location.exchange";
    // 2. 司机位置更新队列（存储位置上报消息，削峰填谷）
    public static final String DRIVER_LOCATION_QUEUE = "driver.location.queue";
    // 3. 路由键（精准绑定交换机和队列，确保消息不迷路）
    public static final String DRIVER_LOCATION_ROUTING_KEY = "driver.location.update.key";
}