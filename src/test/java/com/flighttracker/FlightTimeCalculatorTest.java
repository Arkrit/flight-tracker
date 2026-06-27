package com.flighttracker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.flighttracker.calculator.FlightTimeCalculator;
import com.flighttracker.models.Flight;
import com.flighttracker.models.FlightReport;
import com.flighttracker.models.FlightSpecialist;
import com.flighttracker.models.FlightTrack;
import com.flighttracker.models.MonthlyReport;
import com.flighttracker.models.SpecialistReport;

public class FlightTimeCalculatorTest {

    
    
    public void testSimpleFlight() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight = new Flight("Boeing", "VP-BXX",
            LocalDateTime.of(2020, 12, 15, 8, 0),
            LocalDateTime.of(2020, 12, 15, 12, 0),
            "SVO", "LED", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertEquals(4.0, month.getTotalHours(), 0.01);
        TestRunner.assertFalse(month.isExceeded8HoursPerDay());
        TestRunner.assertFalse(month.isExceeded36HoursPerWeek());
        TestRunner.assertFalse(month.isExceeded80Hours());
    }
    
    
    
    public void testExactly8HoursInDay_NoFlag() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight = new Flight("Boeing", "VP-BXX",
            LocalDateTime.of(2020, 12, 15, 8, 0),
            LocalDateTime.of(2020, 12, 15, 16, 0),
            "SVO", "LED", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertEquals(8.0, month.getTotalHours(), 0.01);
        
        TestRunner.assertFalse(month.isExceeded8HoursPerDay());
        TestRunner.assertEquals(0, month.getDaysExceeded8Hours().size());
    }
    
    public void testMoreThan8HoursInDay_Flag() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight = new Flight("Boeing", "VP-BXX",
            LocalDateTime.of(2020, 12, 15, 8, 0),
            LocalDateTime.of(2020, 12, 15, 16, 1),
            "SVO", "LED", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        
        TestRunner.assertTrue(month.getTotalHours() > 8.0);
        TestRunner.assertTrue(month.isExceeded8HoursPerDay());
        TestRunner.assertEquals(1, month.getDaysExceeded8Hours().size());
        TestRunner.assertEquals("2020-12-15", month.getDaysExceeded8Hours().get(0));
    }
    
    public void testTwoFlightsSameDayExceed8Hours() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight1 = new Flight("Boeing", "VP-AAA",
            LocalDateTime.of(2020, 12, 15, 6, 0),
            LocalDateTime.of(2020, 12, 15, 10, 0),
            "SVO", "LED", Arrays.asList("SP001"));
        Flight flight2 = new Flight("Airbus", "VP-BBB",
            LocalDateTime.of(2020, 12, 15, 14, 0),
            LocalDateTime.of(2020, 12, 15, 19, 0),
            "LED", "SVO", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight1, flight2));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        
        TestRunner.assertEquals(9.0, month.getTotalHours(), 0.01);
        TestRunner.assertTrue(month.isExceeded8HoursPerDay());
        TestRunner.assertEquals(1, month.getDaysExceeded8Hours().size());
    }
    
    public void testFlightOverMidnightEachDayLessThan8() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight = new Flight("Boeing", "VP-XXX",
            LocalDateTime.of(2020, 12, 15, 22, 0),
            LocalDateTime.of(2020, 12, 16, 5, 0),
            "SVO", "DXB", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        
        TestRunner.assertFalse(month.isExceeded8HoursPerDay());
        TestRunner.assertEquals(0, month.getDaysExceeded8Hours().size());
    }
    
    public void testDifferentDaysExceed8Hours() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight1 = new Flight("Boeing", "VP-AAA",
            LocalDateTime.of(2020, 12, 15, 6, 0),
            LocalDateTime.of(2020, 12, 15, 15, 0),
            "SVO", "LED", Arrays.asList("SP001"));
        Flight flight2 = new Flight("Airbus", "VP-BBB",
            LocalDateTime.of(2020, 12, 16, 6, 0),
            LocalDateTime.of(2020, 12, 16, 15, 0),
            "LED", "SVO", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight1, flight2));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertTrue(month.isExceeded8HoursPerDay());
        TestRunner.assertEquals(2, month.getDaysExceeded8Hours().size());
        TestRunner.assertTrue(month.getDaysExceeded8Hours().contains("2020-12-15"));
        TestRunner.assertTrue(month.getDaysExceeded8Hours().contains("2020-12-16"));
    }
    
    
    
    public void testExactly36HoursPerWeek_NoFlag() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        List<Flight> flights = new ArrayList<>();
        for (int day = 14; day <= 19; day++) { 
            flights.add(new Flight("Boeing", "VP-" + day,
                LocalDateTime.of(2020, 12, day, 8, 0),
                LocalDateTime.of(2020, 12, day, 14, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertEquals(36.0, month.getTotalHours(), 0.01);
        
        TestRunner.assertFalse(month.isExceeded36HoursPerWeek());
        TestRunner.assertEquals(0, month.getWeeksExceeded36Hours().size());
    }
    
    public void testMoreThan36HoursPerWeek_Flag() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        List<Flight> flights = new ArrayList<>();
        for (int day = 14; day <= 20; day++) { 
            flights.add(new Flight("Boeing", "VP-" + day,
                LocalDateTime.of(2020, 12, day, 8, 0),
                LocalDateTime.of(2020, 12, day, 14, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertEquals(42.0, month.getTotalHours(), 0.01);
        TestRunner.assertTrue(month.isExceeded36HoursPerWeek());
        TestRunner.assertTrue(month.getWeeksExceeded36Hours().size() > 0);
    }
    
    public void testMultipleWeeksExceeded36Hours() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        
        List<Flight> flights = new ArrayList<>();
        
        
        for (int day = 7; day <= 11; day++) { 
            flights.add(new Flight("Boeing", "VP-A" + day,
                LocalDateTime.of(2020, 12, day, 8, 0),
                LocalDateTime.of(2020, 12, day, 16, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        
        for (int day = 21; day <= 25; day++) { 
            flights.add(new Flight("Boeing", "VP-B" + day,
                LocalDateTime.of(2020, 12, day, 6, 0),
                LocalDateTime.of(2020, 12, day, 16, 0),
                "LED", "SVO", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertTrue(month.isExceeded36HoursPerWeek());
        
        TestRunner.assertTrue(month.getWeeksExceeded36Hours().size() >= 2);
    }
    
    
    
    public void testExactly80HoursPerMonth_NoFlag() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        List<Flight> flights = new ArrayList<>();
        for (int day = 1; day <= 20; day++) {
            flights.add(new Flight("Boeing", "VP-" + day,
                LocalDateTime.of(2020, 12, day, 8, 0),
                LocalDateTime.of(2020, 12, day, 12, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertEquals(80.0, month.getTotalHours(), 0.01);
        
        TestRunner.assertFalse(month.isExceeded80Hours());
    }
    
    public void testMoreThan80HoursPerMonth_Flag() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        List<Flight> flights = new ArrayList<>();
        for (int day = 1; day <= 21; day++) {
            flights.add(new Flight("Boeing", "VP-" + day,
                LocalDateTime.of(2020, 12, day, 8, 0),
                LocalDateTime.of(2020, 12, day, 12, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertTrue(month.getTotalHours() > 80.0);
        TestRunner.assertTrue(month.isExceeded80Hours());
    }
    
    
    
    public void testAllFlagsActive() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        
        
        List<Flight> flights = new ArrayList<>();
        for (int day = 1; day <= 25; day++) {
            flights.add(new Flight("Boeing", "VP-" + day,
                LocalDateTime.of(2020, 12, day, 6, 0),
                LocalDateTime.of(2020, 12, day, 16, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertTrue(month.isExceeded80Hours());
        TestRunner.assertTrue(month.isExceeded36HoursPerWeek());
        TestRunner.assertTrue(month.isExceeded8HoursPerDay());
    }
    
    public void testNoFlagsActive() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        
        
        
        List<Flight> flights = new ArrayList<>();
        for (int day = 1; day <= 10; day++) {
            flights.add(new Flight("Boeing", "VP-" + day,
                LocalDateTime.of(2020, 12, day, 8, 0),
                LocalDateTime.of(2020, 12, day, 12, 0),
                "SVO", "LED", Arrays.asList("SP001")));
        }
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), flights);
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertFalse(month.isExceeded80Hours());
        TestRunner.assertFalse(month.isExceeded36HoursPerWeek());
        TestRunner.assertFalse(month.isExceeded8HoursPerDay());
    }
    
    
    
    public void testMultipleSpecialistsDifferentHours() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec1 = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        FlightSpecialist spec2 = new FlightSpecialist("SP002", "Petr", "Petrov", "Pilot");
        
        
        Flight flight1 = new Flight("Boeing", "VP-AAA",
            LocalDateTime.of(2020, 12, 15, 6, 0),
            LocalDateTime.of(2020, 12, 15, 16, 0),
            "SVO", "LED", Arrays.asList("SP001"));
        
        
        Flight flight2 = new Flight("Airbus", "VP-BBB",
            LocalDateTime.of(2020, 12, 15, 8, 0),
            LocalDateTime.of(2020, 12, 15, 12, 0),
            "LED", "SVO", Arrays.asList("SP002"));
        
        FlightTrack data = new FlightTrack(
            Arrays.asList(spec1, spec2),
            Arrays.asList(flight1, flight2));
        
        FlightReport report = calc.calculate(data);
        
        
        MonthlyReport report1 = null;
        MonthlyReport report2 = null;
        
        for (SpecialistReport sr : report.getSpecialists()) {
            if (sr.getId().equals("SP001")) {
                report1 = sr.getMonthlyReports().get(0);
            } else if (sr.getId().equals("SP002")) {
                report2 = sr.getMonthlyReports().get(0);
            }
        }
        
        TestRunner.assertNotNull(report1);
        TestRunner.assertNotNull(report2);
        
        TestRunner.assertEquals(10.0, report1.getTotalHours(), 0.01);
        TestRunner.assertTrue(report1.isExceeded8HoursPerDay());
        
        TestRunner.assertEquals(4.0, report2.getTotalHours(), 0.01);
        TestRunner.assertFalse(report2.isExceeded8HoursPerDay());
    }
    
    
    
    public void testFlightSpanningNewYear() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight = new Flight("Boeing", "VP-YYY",
            LocalDateTime.of(2020, 12, 31, 22, 0),
            LocalDateTime.of(2021, 1, 1, 6, 0),
            "SVO", "DXB", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        TestRunner.assertEquals(2, report.getMonths().size());
        TestRunner.assertTrue(report.getMonths().contains("2020-12"));
        TestRunner.assertTrue(report.getMonths().contains("2021-01"));
        
        SpecialistReport specReport = report.getSpecialists().get(0);
        TestRunner.assertEquals(2, specReport.getMonthlyReports().size());
        
        
        MonthlyReport dec = specReport.getMonthlyReports().get(0);
        TestRunner.assertEquals("2020-12", dec.getMonth());
        TestRunner.assertEquals(2.0, dec.getTotalHours(), 0.01);
        
        
        MonthlyReport jan = specReport.getMonthlyReports().get(1);
        TestRunner.assertEquals("2021-01", jan.getMonth());
        TestRunner.assertEquals(6.0, jan.getTotalHours(), 0.01);
    }
    
    public void testFlightSpanningThreeMonths() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        Flight flight = new Flight("Boeing", "VP-ZZZ",
            LocalDateTime.of(2021, 1, 31, 23, 0),
            LocalDateTime.of(2021, 2, 1, 1, 0),
            "SVO", "DXB", Arrays.asList("SP001"));
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        
        TestRunner.assertEquals(2, report.getMonths().size());
        
        SpecialistReport specReport = report.getSpecialists().get(0);
        
        
        MonthlyReport jan = specReport.getMonthlyReports().get(0);
        TestRunner.assertEquals(1.0, jan.getTotalHours(), 0.01);
        
        
        MonthlyReport feb = specReport.getMonthlyReports().get(1);
        TestRunner.assertEquals(1.0, feb.getTotalHours(), 0.01);
    }
    
    
    
    public void testSpecialistWithNoFlights() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        Flight flight = new Flight("Boeing", "VP-AAA",
            LocalDateTime.of(2020, 12, 15, 8, 0),
            LocalDateTime.of(2020, 12, 15, 12, 0),
            "SVO", "LED", Arrays.asList("SP002")); 
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), Arrays.asList(flight));
        FlightReport report = calc.calculate(data);
        
        MonthlyReport month = report.getSpecialists().get(0).getMonthlyReports().get(0);
        TestRunner.assertEquals(0.0, month.getTotalHours(), 0.01);
        TestRunner.assertFalse(month.isExceeded8HoursPerDay());
        TestRunner.assertFalse(month.isExceeded36HoursPerWeek());
        TestRunner.assertFalse(month.isExceeded80Hours());
    }
    
    public void testNoFlightsAtAll() {
        FlightTimeCalculator calc = new FlightTimeCalculator();
        
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        FlightTrack data = new FlightTrack(Arrays.asList(spec), new ArrayList<>());
        FlightReport report = calc.calculate(data);
        
        
        TestRunner.assertEquals(0, report.getMonths().size());
        TestRunner.assertEquals(1, report.getSpecialists().size());
        TestRunner.assertEquals(0, report.getSpecialists().get(0).getMonthlyReports().size());
    }
}