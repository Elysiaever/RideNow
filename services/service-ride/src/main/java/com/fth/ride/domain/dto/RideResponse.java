package com.fth.ride.domain.dto;

import com.fth.ride.domain.model.Ride;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class RideResponse {
    private String rideId;
    private String passengerId;
    private String driverId;
    private String orginAddress;
    private double originLat;
    private double originLng;
    private String destAddress;
    private double destLat;
    private double destLng;
    private String requestTime;
    private String status;
    private double price;
    private double distance;
    private double duration;

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static RideResponse from(Ride r) {
        RideResponse resp = new RideResponse();
        resp.setRideId(r.getRideId());
        resp.setPassengerId(r.getPassengerId());
        resp.setDriverId(r.getDriverId());
        resp.setOrginAddress(r.getOriginAddress());
        resp.setOriginLat(r.getOriginLat());
        resp.setOriginLng(r.getOriginLng());
        resp.setDestAddress(r.getDestAddress());
        resp.setDestLat(r.getDestLat());
        resp.setDestLng(r.getDestLng());
        resp.setStatus(r.getStatus());
        resp.setPrice(r.getPrice());
        resp.setDistance(r.getDistance());
        resp.setDuration(r.getDuration());

        if (r.getRequestTime() != null) {
            resp.setRequestTime(r.getRequestTime().format(fmt));
        }

        return resp;
    }
}