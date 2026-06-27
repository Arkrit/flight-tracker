package com.flighttracker.calculator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.flighttracker.models.Flight;
import com.flighttracker.models.FlightReport;
import com.flighttracker.models.FlightSpecialist;
import com.flighttracker.models.FlightTrack;
import com.flighttracker.models.MonthlyReport;
import com.flighttracker.models.SpecialistReport;

public class FlightTimeCalculator {

    public FlightReport calculate(FlightTrack data) {
        
        Set<String> allMonths = new TreeSet<>();
        for (Flight flight : data.getFlights()) {
            allMonths.add(getMonth(flight.getDepartureTime()));
            allMonths.add(getMonth(flight.getArrivalTime()));
        }
        
        List<String> monthList = new ArrayList<>(allMonths);
        
        
        List<SpecialistReport> specReports = new ArrayList<>();
        for (FlightSpecialist spec : data.getSpecialists()) {
            specReports.add(makeReport(spec, monthList, data.getFlights()));
        }
        
        return new FlightReport(monthList, specReports);
    }
    
    private String getMonth(LocalDateTime time) {
        int year = time.getYear();
        int month = time.getMonthValue();
        String m = month < 10 ? "0" + month : "" + month;
        return year + "-" + m;
    }
    
    private SpecialistReport makeReport(FlightSpecialist spec, List<String> months, List<Flight> allFlights) {
        
        List<Flight> myFlights = new ArrayList<>();
        for (Flight f : allFlights) {
            if (f.getCrewIds().contains(spec.getId())) {
                myFlights.add(f);
            }
        }
        
        
        List<MonthlyReport> monthlyReports = new ArrayList<>();
        for (String month : months) {
            monthlyReports.add(calcMonth(spec, month, myFlights));
        }
        
        return new SpecialistReport(spec.getId(), spec.getFullName(), spec.getPosition(), monthlyReports);
    }
    
    private MonthlyReport calcMonth(FlightSpecialist spec, String month, List<Flight> flights) {
        
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int mon = Integer.parseInt(parts[1]);
        
        LocalDate monthStart = LocalDate.of(year, mon, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        
        double totalHours = 0;
        
        
        Map<String, Double> dayMap = new TreeMap<>();
        Map<Integer, Double> weekMap = new TreeMap<>();
        WeekFields wf = WeekFields.ISO;
        
        for (Flight flight : flights) {
            
            LocalDate flightStart = flight.getDepartureTime().toLocalDate();
            LocalDate flightEnd = flight.getArrivalTime().toLocalDate();
            
            if (flightEnd.isBefore(monthStart) || flightStart.isAfter(monthEnd)) {
                continue; 
            }
            
            
            LocalDateTime start = flight.getDepartureTime();
            LocalDateTime end = flight.getArrivalTime();
            
            if (start.toLocalDate().isBefore(monthStart)) {
                start = monthStart.atStartOfDay();
            }
            if (end.toLocalDate().isAfter(monthEnd)) {
                end = monthEnd.plusDays(1).atStartOfDay();
            }
            
            
            long minutes = Duration.between(start, end).toMinutes();
            double hours = minutes / 60.0;
            totalHours += hours;
            
            
            LocalDate date = start.toLocalDate();
            LocalDateTime time = start;
            
            while (time.isBefore(end)) {
                LocalDateTime nextDay = date.plusDays(1).atStartOfDay();
                if (nextDay.isAfter(end)) {
                    nextDay = end;
                }
                
                long dayMinutes = Duration.between(time, nextDay).toMinutes();
                double dayHours = dayMinutes / 60.0;
                
                
                String dayKey = date.toString();
                Double old = dayMap.get(dayKey);
                if (old == null) {
                    dayMap.put(dayKey, dayHours);
                } else {
                    dayMap.put(dayKey, old + dayHours);
                }
                
                
                int weekNum = date.get(wf.weekOfWeekBasedYear());
                Double oldWeek = weekMap.get(weekNum);
                if (oldWeek == null) {
                    weekMap.put(weekNum, dayHours);
                } else {
                    weekMap.put(weekNum, oldWeek + dayHours);
                }
                
                date = date.plusDays(1);
                time = nextDay;
            }
        }
        
        
        boolean exceeded80 = totalHours > 80;
        
        boolean exceeded8 = false;
        List<String> days8 = new ArrayList<>();
        for (Map.Entry<String, Double> entry : dayMap.entrySet()) {
            if (entry.getValue() > 8) {
                exceeded8 = true;
                days8.add(entry.getKey());
            }
        }
        
        boolean exceeded36 = false;
        List<String> weeks36 = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : weekMap.entrySet()) {
            if (entry.getValue() > 36) {
                exceeded36 = true;
                String w = String.format("%d-W%02d", year, entry.getKey());
                weeks36.add(w);
            }
        }
        
        return new MonthlyReport(spec.getId(), month, totalHours, exceeded80, exceeded36, exceeded8, weeks36, days8);
    }
}