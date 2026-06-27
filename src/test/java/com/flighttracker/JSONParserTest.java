// JSONParserTest.java
package com.flighttracker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import com.flighttracker.models.Flight;
import com.flighttracker.models.FlightTrack;
import com.flighttracker.parser.JSONParser;

public class JSONParserTest {
    
    public void testParseOneSpecialist() throws Exception {
        String json = "{\n" +
            "  \"specialists\": [\n" +
            "    {\n" +
            "      \"id\": \"SP001\",\n" +
            "      \"firstName\": \"Ivan\",\n" +
            "      \"lastName\": \"Ivanov\",\n" +
            "      \"position\": \"Captain\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"flights\": []\n" +
            "}";
        
        Path file = createTempFile(json);
        JSONParser parser = new JSONParser();
        FlightTrack data = parser.parse(file);
        
        TestRunner.assertEquals(1, data.getSpecialists().size());
        TestRunner.assertEquals("SP001", data.getSpecialists().get(0).getId());
        TestRunner.assertEquals("Ivan", data.getSpecialists().get(0).getFirstName());
        TestRunner.assertEquals("Captain", data.getSpecialists().get(0).getPosition());
        
        Files.delete(file);
    }
    
    public void testParseOneFlight() throws Exception {
        String json = "{\n" +
            "  \"specialists\": [\n" +
            "    {\n" +
            "      \"id\": \"SP001\",\n" +
            "      \"firstName\": \"Ivan\",\n" +
            "      \"lastName\": \"Ivanov\",\n" +
            "      \"position\": \"Captain\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"flights\": [\n" +
            "    {\n" +
            "      \"aircraftType\": \"Boeing 737\",\n" +
            "      \"aircraftNumber\": \"VP-BXX\",\n" +
            "      \"departureTime\": \"2020-12-15T08:30:00\",\n" +
            "      \"arrivalTime\": \"2020-12-15T12:45:00\",\n" +
            "      \"departureAirport\": \"SVO\",\n" +
            "      \"arrivalAirport\": \"LED\",\n" +
            "      \"crewIds\": [\"SP001\"]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        
        Path file = createTempFile(json);
        JSONParser parser = new JSONParser();
        FlightTrack data = parser.parse(file);
        
        TestRunner.assertEquals(1, data.getFlights().size());
        
        Flight flight = data.getFlights().get(0);
        TestRunner.assertEquals("Boeing 737", flight.getAircraftType());
        TestRunner.assertEquals("VP-BXX", flight.getAircraftNumber());
        TestRunner.assertEquals(LocalDateTime.of(2020, 12, 15, 8, 30), flight.getDepartureTime());
        TestRunner.assertEquals("SVO", flight.getDepartureAirport());
        TestRunner.assertEquals("LED", flight.getArrivalAirport());
        
        Files.delete(file);
    }
    
    public void testFlightDuration() throws Exception {
        String json = "{" +
            "\"specialists\":[]," +
            "\"flights\":[" +
            "{" +
            "\"aircraftType\":\"Boeing 737\"," +
            "\"aircraftNumber\":\"VP-BXX\"," +
            "\"departureTime\":\"2020-12-15T08:00:00\"," +
            "\"arrivalTime\":\"2020-12-15T12:00:00\"," +
            "\"departureAirport\":\"SVO\"," +
            "\"arrivalAirport\":\"LED\"," +
            "\"crewIds\":[]" +
            "}" +
            "]" +
            "}";
        
        Path file = createTempFile(json);
        JSONParser parser = new JSONParser();
        FlightTrack data = parser.parse(file);
        
        double hours = data.getFlights().get(0).getDurationHours();
        TestRunner.assertEquals(4.0, hours, 0.01);
        
        Files.delete(file);
    }
    
    public void testParseMultipleFlights() throws Exception {
        String json = "{" +
            "\"specialists\":[" +
            "{\"id\":\"SP001\",\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"position\":\"Captain\"}" +
            "]," +
            "\"flights\":[" +
            "{" +
            "\"aircraftType\":\"Boeing 737\"," +
            "\"aircraftNumber\":\"VP-AAA\"," +
            "\"departureTime\":\"2020-12-15T08:00:00\"," +
            "\"arrivalTime\":\"2020-12-15T12:00:00\"," +
            "\"departureAirport\":\"SVO\"," +
            "\"arrivalAirport\":\"LED\"," +
            "\"crewIds\":[\"SP001\"]" +
            "}," +
            "{" +
            "\"aircraftType\":\"Airbus A320\"," +
            "\"aircraftNumber\":\"VP-BBB\"," +
            "\"departureTime\":\"2021-01-10T08:00:00\"," +
            "\"arrivalTime\":\"2021-01-10T12:00:00\"," +
            "\"departureAirport\":\"LED\"," +
            "\"arrivalAirport\":\"SVO\"," +
            "\"crewIds\":[\"SP001\"]" +
            "}" +
            "]" +
            "}";
        
        Path file = createTempFile(json);
        JSONParser parser = new JSONParser();
        FlightTrack data = parser.parse(file);
        
        TestRunner.assertEquals(2, data.getFlights().size());
        TestRunner.assertEquals("Boeing 737", data.getFlights().get(0).getAircraftType());
        TestRunner.assertEquals("Airbus A320", data.getFlights().get(1).getAircraftType());
        
        Files.delete(file);
    }
    
    private Path createTempFile(String content) throws Exception {
        Path path = Files.createTempFile("test", ".json");
        Files.write(path, content.getBytes());
        return path;
    }
}