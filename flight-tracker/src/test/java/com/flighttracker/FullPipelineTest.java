package com.flighttracker;

import java.nio.file.Files;
import java.nio.file.Path;

import com.flighttracker.calculator.FlightTimeCalculator;
import com.flighttracker.models.FlightReport;
import com.flighttracker.models.FlightTrack;
import com.flighttracker.parser.JSONParser;
import com.flighttracker.report.ReportGenerator;

public class FullPipelineTest {
    
    public void testFullPipelineJsonToJson() throws Exception {
        String inputJson = "{\"specialists\":[" +
            "{\"id\":\"SP001\",\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"position\":\"Captain\"}," +
            "{\"id\":\"SP002\",\"firstName\":\"Petr\",\"lastName\":\"Petrov\",\"position\":\"Pilot\"}" +
            "],\"flights\":[" +
            "{\"aircraftType\":\"Boeing 737\",\"aircraftNumber\":\"VP-BXX\"," +
            "\"departureTime\":\"2020-12-15T08:00:00\",\"arrivalTime\":\"2020-12-15T17:00:00\"," +
            "\"departureAirport\":\"SVO\",\"arrivalAirport\":\"LED\",\"crewIds\":[\"SP001\",\"SP002\"]}," +
            "{\"aircraftType\":\"Airbus A320\",\"aircraftNumber\":\"VP-CCC\"," +
            "\"departureTime\":\"2021-01-10T08:00:00\",\"arrivalTime\":\"2021-01-10T12:00:00\"," +
            "\"departureAirport\":\"LED\",\"arrivalAirport\":\"SVO\",\"crewIds\":[\"SP001\"]}" +
            "]}";
        
        Path inputFile = Files.createTempFile("input", ".json");
        Files.write(inputFile, inputJson.getBytes());
        
        JSONParser parser = new JSONParser();
        FlightTrack data = parser.parse(inputFile);
        
        TestRunner.assertEquals(2, data.getSpecialists().size());
        TestRunner.assertEquals(2, data.getFlights().size());
        
        FlightTimeCalculator calculator = new FlightTimeCalculator();
        FlightReport report = calculator.calculate(data);
        
        TestRunner.assertEquals(2, report.getMonths().size());
        TestRunner.assertEquals(2, report.getSpecialists().size());
        
        Path outputFile = Files.createTempFile("output", ".json");
        ReportGenerator generator = new ReportGenerator();
        generator.generateJson(report, outputFile);
        
        String output = new String(Files.readAllBytes(outputFile));
        TestRunner.assertTrue(output.contains("SP001"));
        TestRunner.assertTrue(output.contains("SP002"));
        
        Files.delete(inputFile);
        Files.delete(outputFile);
    }
}