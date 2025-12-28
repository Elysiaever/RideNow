package com.fth.common.feign.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "service-driver", path = "/api/driver")
public interface DriverFeignClient {

    @GetMapping("/is-driver")
    boolean isDriver(@RequestParam("userId") Long userId);
}
