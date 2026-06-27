package com.flighttracker;

import java.nio.file.Files;
import java.nio.file.Path;

import com.flighttracker.models.Flight;
import com.flighttracker.models.FlightTrack;
import com.flighttracker.parser.XMLParser;

public class XMLParserTest {
    
    public void testParseSpecialist() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<FlightTrack>" +
            "<specialists>" +
            "<specialist>" +
            "<id>SP001</id>" +
            "<firstName>Ivan</firstName>" +
            "<lastName>Ivanov</lastName>" +
            "<position>Captain</position>" +
            "</specialist>" +
            "</specialists>" +
            "<flights></flights>" +
            "</FlightTrack>";
        
        Path file = createTempFile(xml);
        XMLParser parser = new XMLParser();
        FlightTrack data = parser.parse(file);
        
        TestRunner.assertEquals(1, data.getSpecialists().size());
        TestRunner.assertEquals("SP001", data.getSpecialists().get(0).getId());
        TestRunner.assertEquals("Captain", data.getSpecialists().get(0).getPosition());
        
        Files.delete(file);
    }
    
    public void testParseFlight() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<FlightTrack>" +
            "<specialists></specialists>" +
            "<flights>" +
            "<flight>" +
            "<aircraftType>Airbus A320</aircraftType>" +
            "<aircraftNumber>VP-CCC</aircraftNumber>" +
            "<departureTime>2021-01-10T08:00:00</departureTime>" +
            "<arrivalTime>2021-01-10T12:00:00</arrivalTime>" +
            "<departureAirport>DXB</departureAirport>" +
            "<arrivalAirport>SVO</arrivalAirport>" +
            "<crew>" +
            "<crewId>SP001</crewId>" +
            "<crewId>SP002</crewId>" +
            "</crew>" +
            "</flight>" +
            "</flights>" +
            "</FlightTrack>";
        
        Path file = createTempFile(xml);
        XMLParser parser = new XMLParser();
        FlightTrack data = parser.parse(file);
        
        TestRunner.assertEquals(1, data.getFlights().size());
        
        Flight flight = data.getFlights().get(0);
        TestRunner.assertEquals("Airbus A320", flight.getAircraftType());
        TestRunner.assertEquals(2, flight.getCrewIds().size());
        TestRunner.assertTrue(flight.getCrewIds().contains("SP001"));
        TestRunner.assertTrue(flight.getCrewIds().contains("SP002"));
        
        Files.delete(file);
    }
    
    private Path createTempFile(String content) throws Exception {
        Path path = Files.createTempFile("test", ".xml");
        Files.write(path, content.getBytes());
        return path;
    }
}