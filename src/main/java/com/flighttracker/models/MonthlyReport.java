package com.flighttracker.models;

import java.util.List;

public class MonthlyReport {
    private final String specialistId;
    private final String month; 
    private final double totalHours;
    private final boolean exceeded80Hours;
    private final boolean exceeded36HoursPerWeek;
    private final boolean exceeded8HoursPerDay;
    private final List<String> weeksExceeded36Hours;
    private final List<String> daysExceeded8Hours;

    public MonthlyReport(String specialistId, String month, double totalHours,
                        boolean exceeded80Hours, boolean exceeded36HoursPerWeek,
                        boolean exceeded8HoursPerDay, List<String> weeksExceeded36Hours,
                        List<String> daysExceeded8Hours) {
        this.specialistId = specialistId;
        this.month = month;
        this.totalHours = totalHours;
        this.exceeded80Hours = exceeded80Hours;
        this.exceeded36HoursPerWeek = exceeded36HoursPerWeek;
        this.exceeded8HoursPerDay = exceeded8HoursPerDay;
        this.weeksExceeded36Hours = weeksExceeded36Hours;
        this.daysExceeded8Hours = daysExceeded8Hours;
    }

    public String getSpecialistId() { return specialistId; }
    public String getMonth() { return month; }
    public double getTotalHours() { return totalHours; }
    public boolean isExceeded80Hours() { return exceeded80Hours; }
    public boolean isExceeded36HoursPerWeek() { return exceeded36HoursPerWeek; }
    public boolean isExceeded8HoursPerDay() { return exceeded8HoursPerDay; }
    public List<String> getWeeksExceeded36Hours() { return weeksExceeded36Hours; }
    public List<String> getDaysExceeded8Hours() { return daysExceeded8Hours; }
}
