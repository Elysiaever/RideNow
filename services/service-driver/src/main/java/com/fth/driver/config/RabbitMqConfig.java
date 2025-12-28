package com.fth.driver.config;

import com.fth.driver.constant.RabbitMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 队列/交换机/绑定 配置类
 * 启动项目自动创建对应的交换机、队列，无需手动在RabbitMQ管理界面创建
 */
/**
 * RabbitMQ 队列/交换机/绑定 配置类（低版本兼容版，无builder()方法）
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 声明：司机位置更新队列（使用传统构造方法，兼容低版本Spring AMQP）
     * 构造方法参数说明：Queue(String name, boolean durable)
     * - name：队列名称
     * - durable：是否持久化（true=持久化，RabbitMQ重启后队列不丢失，与原需求一致）
     */
    @Bean
    public Queue driverLocationQueue() {
        // 替换 builder() 为 传统构造方法，功能完全一致
        return new Queue(RabbitMqConstant.DRIVER_LOCATION_QUEUE, true);
    }

    /**
     * 声明：司机位置更新交换机（使用传统构造方法，兼容低版本Spring AMQP）
     * 构造方法参数说明：DirectExchange(String name, boolean durable, boolean autoDelete)
     * - name：交换机名称
     * - durable：是否持久化（true=持久化）
     * - autoDelete：是否自动删除（false=不自动删除，长期有效）
     */
    @Bean
    public DirectExchange driverLocationExchange() {
        // 替换 builder() 为 传统构造方法，功能完全一致
        return new DirectExchange(RabbitMqConstant.DRIVER_LOCATION_EXCHANGE, true, false);
    }

    /**
     * 绑定：队列 ← 路由键 ← 交换机（该方法与原代码一致，无需修改）
     */
    @Bean
    public Binding driverLocationBinding(Queue driverLocationQueue, DirectExchange driverLocationExchange) {
        return BindingBuilder.bind(driverLocationQueue)
                .to(driverLocationExchange)
                .with(RabbitMqConstant.DRIVER_LOCATION_ROUTING_KEY);
    }
}