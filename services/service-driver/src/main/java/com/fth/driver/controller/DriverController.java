package com.fth.driver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Driver Service";
    }
}
