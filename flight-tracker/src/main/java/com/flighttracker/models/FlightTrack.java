package com.flighttracker.models;

import java.util.Collections;
import java.util.List;

public class FlightTrack {
    private final List<FlightSpecialist> specialists;
    private final List<Flight> flights;

    public FlightTrack(List<FlightSpecialist> specialists, List<Flight> flights) {
        this.specialists = Collections.unmodifiableList(specialists);
        this.flights = Collections.unmodifiableList(flights);
    }

    public List<FlightSpecialist> getSpecialists() { return specialists; }
    public List<Flight> getFlights() { return flights; }
}
