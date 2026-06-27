package com.flighttracker.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.flighttracker.models.Flight;
import com.flighttracker.models.FlightSpecialist;
import com.flighttracker.models.FlightTrack;

public class XMLParser implements FlightParser {

    @Override
    public FlightTrack parse(Path filePath) throws IOException {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(filePath.toFile());
            
            List<FlightSpecialist> specialists = parseSpecialists(doc);
            List<Flight> flights = parseFlights(doc);
            
            return new FlightTrack(specialists, flights);
        } catch (Exception e) {
            throw new IOException("Failed to parse XML", e);
        }
    }

    private List<FlightSpecialist> parseSpecialists(Document doc) {
        List<FlightSpecialist> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("specialist");
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            result.add(new FlightSpecialist(
                getText(e, "id"),
                getText(e, "firstName"),
                getText(e, "lastName"),
                getText(e, "position")
            ));
        }
        return result;
    }

    private List<Flight> parseFlights(Document doc) {
        List<Flight> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("flight");
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            result.add(new Flight(
                getText(e, "aircraftType"),
                getText(e, "aircraftNumber"),
                LocalDateTime.parse(getText(e, "departureTime")),
                LocalDateTime.parse(getText(e, "arrivalTime")),
                getText(e, "departureAirport"),
                getText(e, "arrivalAirport"),
                parseCrewIds(e)
            ));
        }
        return result;
    }

    private List<String> parseCrewIds(Element flightElement) {
        List<String> crewIds = new ArrayList<>();
        NodeList crewNodes = flightElement.getElementsByTagName("crewId");
        for (int i = 0; i < crewNodes.getLength(); i++) {
            crewIds.add(crewNodes.item(i).getTextContent().trim());
        }
        return crewIds;
    }

    private String getText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException("Missing element: " + tagName);
        }
        return nodes.item(0).getTextContent().trim();
    }
}