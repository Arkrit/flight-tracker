package com.flighttracker.models;

import java.util.*;

public class FlightReport {
    private final List<String> months;
    private final List<SpecialistReport> specialists;

    public FlightReport(List<String> months, List<SpecialistReport> specialists) {
        this.months = months;
        this.specialists = specialists;
    }

    public List<String> getMonths() { return months; }
    public List<SpecialistReport> getSpecialists() { return specialists; }
}
