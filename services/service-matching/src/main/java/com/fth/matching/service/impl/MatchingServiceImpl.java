package com.fth.matching.service.impl;

import com.fth.matching.service.MatchingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingServiceImpl implements MatchingService {

    @Override
    public List<?> findBestMatchingDrivers(double lng,double lat) {
        return List.of();
    }
}
