package com.fth.ride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fth.ride",          // 本模块的包（默认扫描范围）
        "com.fth.common"         // 手动添加 common 模块的根包，确保其子包被扫描
})
public class RideMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(RideMainApplication.class, args);
    }
}