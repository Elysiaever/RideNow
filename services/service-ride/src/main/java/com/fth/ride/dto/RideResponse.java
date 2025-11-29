package com.fth.ride.dto;

import com.fth.ride.model.Ride;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class RideResponse {
    private String rideId;
    private String passengerId;
    private String driverId;
    private String origin;
    private String destination;
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
        resp.setOrigin(r.getOrigin());
        resp.setDestination(r.getDestination());
        resp.setStatus(r.getStatus().name());
        resp.setPrice(r.getPrice());
        resp.setDistance(r.getDistance());
        resp.setDuration(r.getDuration());

        if (r.getRequestTime() != null) {
            resp.setRequestTime(r.getRequestTime().format(fmt));
        }

        return resp;
    }
}