package com.fth.driver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.fth.common","com.fth.driver"})
@MapperScan(basePackages = "com.fth.driver.mapper",
        annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class DriverMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverMainApplication.class, args);
    }
}
