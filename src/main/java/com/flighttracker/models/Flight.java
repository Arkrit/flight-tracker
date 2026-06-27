package com.flighttracker.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Flight {
    private final String aircraftType;
    private final String aircraftNumber;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final String departureAirport;
    private final String arrivalAirport;
    private final List<String> crewIds;

    public Flight(String aircraftType, String aircraftNumber,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  String departureAirport, String arrivalAirport,
                  List<String> crewIds) {
        this.aircraftType = aircraftType;
        this.aircraftNumber = aircraftNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.crewIds = Collections.unmodifiableList(crewIds);
    }

    public String getAircraftType() { return aircraftType; }
    public String getAircraftNumber() { return aircraftNumber; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public String getDepartureAirport() { return departureAirport; }
    public String getArrivalAirport() { return arrivalAirport; }
    public List<String> getCrewIds() { return crewIds; }
    
    public double getDurationHours() {
        return Duration.between(departureTime, arrivalTime).toMinutes() / 60.0;
    }
}
