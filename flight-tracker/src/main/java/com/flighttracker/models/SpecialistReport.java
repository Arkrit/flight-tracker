package com.flighttracker.models;

import java.util.List;

public class SpecialistReport {
    private final String id;
    private final String name;
    private final String position;
    private final List<MonthlyReport> monthlyReports;

    public SpecialistReport(String id, String name, String position, List<MonthlyReport> monthlyReports) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.monthlyReports = monthlyReports;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public List<MonthlyReport> getMonthlyReports() { return monthlyReports; }
}
