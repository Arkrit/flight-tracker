package com.flighttracker.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flighttracker.models.Flight;
import com.flighttracker.models.FlightSpecialist;
import com.flighttracker.models.FlightTrack;

public class JSONParser implements FlightParser {

    @Override
    public FlightTrack parse(Path filePath) throws IOException {
        String json = new String(Files.readAllBytes(filePath));
        return parseString(json);
    }
    
    public FlightTrack parseString(String json) {
        Map<String, Object> root = parseObject(new Tokenizer(json));
        
        List<FlightSpecialist> specialists = parseSpecialists(root);
        List<Flight> flights = parseFlights(root);
        
        return new FlightTrack(specialists, flights);
    }

    @SuppressWarnings("unchecked")
    private List<FlightSpecialist> parseSpecialists(Map<String, Object> root) {
        List<FlightSpecialist> result = new ArrayList<>();
        List<Map<String, Object>> list = (List<Map<String, Object>>) root.get("specialists");
        
        for (Map<String, Object> m : list) {
            result.add(new FlightSpecialist(
                (String) m.get("id"),
                (String) m.get("firstName"),
                (String) m.get("lastName"),
                (String) m.get("position")
            ));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Flight> parseFlights(Map<String, Object> root) {
        List<Flight> result = new ArrayList<>();
        List<Map<String, Object>> list = (List<Map<String, Object>>) root.get("flights");
        
        for (Map<String, Object> m : list) {
            result.add(new Flight(
                (String) m.get("aircraftType"),
                (String) m.get("aircraftNumber"),
                LocalDateTime.parse((String) m.get("departureTime")),
                LocalDateTime.parse((String) m.get("arrivalTime")),
                (String) m.get("departureAirport"),
                (String) m.get("arrivalAirport"),
                (List<String>) m.get("crewIds")
            ));
        }
        return result;
    }

    
    
    private Map<String, Object> parseObject(Tokenizer t) {
        Map<String, Object> map = new LinkedHashMap<>();
        t.skip('{');
        if (t.peek() == '}') { t.next(); return map; }
        
        while (true) {
            String key = parseString(t);
            t.skip(':');
            map.put(key, parseValue(t));
            char c = t.next();
            if (c == '}') break;
        }
        return map;
    }

    private List<Object> parseArray(Tokenizer t) {
        List<Object> list = new ArrayList<>();
        t.skip('[');
        if (t.peek() == ']') { t.next(); return list; }
        
        while (true) {
            list.add(parseValue(t));
            char c = t.next();
            if (c == ']') break;
        }
        return list;
    }

    private Object parseValue(Tokenizer t) {
        char c = t.peek();
        if (c == '"') return parseString(t);
        if (c == '{') return parseObject(t);
        if (c == '[') return parseArray(t);
        if (c == 't' || c == 'f') return parseBoolean(t);
        if (c == 'n') { t.skip("null"); return null; }
        return parseNumber(t);
    }

    private String parseString(Tokenizer t) {
        t.skip('"');
        StringBuilder sb = new StringBuilder();
        while (t.peekRaw() != '"') {
            char c = t.nextRaw();
            if (c == '\\') sb.append(escape(t.next()));
            else sb.append(c);
        }
        t.skip('"');
        return sb.toString();
    }

    private char escape(char c) {
        switch (c) {
            case '"': return '"';
            case '\\': return '\\';
            case 'n': return '\n';
            case 't': return '\t';
            default: return c;
        }
    }

    private Boolean parseBoolean(Tokenizer t) {
        if (t.peek() == 't') { t.skip("true"); return true; }
        t.skip("false"); return false;
    }

    private Number parseNumber(Tokenizer t) {
        StringBuilder sb = new StringBuilder();
        while (t.hasMore() && (Character.isDigit(t.peek()) || t.peek() == '.' || t.peek() == '-')) {
            sb.append(t.next());
        }
        String s = sb.toString();
        return s.contains(".") ? Double.parseDouble(s) : Long.parseLong(s);
    }

    
    
    private static class Tokenizer {
        private final String s;
        private int pos;

        Tokenizer(String s) { this.s = s; this.pos = 0;}

        char peek() { skipWhitespace(); return s.charAt(pos); }
        char peekRaw() { return s.charAt(pos); }
        char next() { skipWhitespace(); return s.charAt(pos++); }
        char nextRaw() { return s.charAt(pos++); }
        boolean hasMore() { skipWhitespace(); return pos < s.length(); }

        void skip(char expected) {
            if (next() != expected) throw new IllegalArgumentException("Expected '" + expected + "'");
        }

        void skip(String expected) {
            for (char c : expected.toCharArray()) {
                if (next() != c) throw new IllegalArgumentException("Expected: " + expected);
            }
        }

        private void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }
    }
}