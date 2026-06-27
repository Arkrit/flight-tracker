package com.flighttracker.parser;

import java.io.IOException;
import java.nio.file.Path;

import com.flighttracker.models.FlightTrack;

public interface FlightParser {
    FlightTrack parse(Path filePath) throws IOException;
}
