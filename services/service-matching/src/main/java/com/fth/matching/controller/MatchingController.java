package com.fth.matching.controller;

import com.fth.common.api.Response;
import com.fth.matching.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;


    @GetMapping("/findBest")
    public Response<?> findBest(double lng, double lat){

        return Response.success(matchingService.findBestMatchingDrivers(lng, lat));
    }
}
