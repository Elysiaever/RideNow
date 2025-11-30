package com.fth.matching;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchingMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MatchingMainApplication.class, args);
    }
}
