package com.fth.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.fth.common","com.fth.driver"})
public class DriverMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverMainApplication.class, args);
    }
}
