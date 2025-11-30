package com.fth.ride;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fth.ride", "com.fth.common.security", "com.fth.common"})
@MapperScan(basePackages = "com.fth.ride.mapper")
public class RideMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(RideMainApplication.class, args);
    }
}