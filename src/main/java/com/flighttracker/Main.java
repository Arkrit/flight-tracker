package com.flighttracker;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.flighttracker.calculator.FlightTimeCalculator;
import com.flighttracker.models.FlightReport;
import com.flighttracker.models.FlightTrack;
import com.flighttracker.parser.FlightParser;
import com.flighttracker.parser.JSONParser;
import com.flighttracker.parser.XMLParser;
import com.flighttracker.report.ReportGenerator;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Использование: java -jar flight-tracker.jar input.json output.json");
            System.out.println("Или: java -jar flight-tracker.jar input.xml output.xml");
            return;
        }
        
        try {
            Path inputPath = Paths.get(args[0]);
            Path outputPath = Paths.get(args[1]);
            
            
            String inputName = inputPath.toString().toLowerCase();
            FlightParser parser;
            if (inputName.endsWith(".json")) {
                parser = new JSONParser();
            } else if (inputName.endsWith(".xml")) {
                parser = new XMLParser();
            } else {
                System.err.println("Ошибка: файл должен быть .json или .xml");
                return;
            }
            
            
            System.out.println("Чтение файла: " + inputPath);
            FlightTrack data = parser.parse(inputPath);
            System.out.println("Загружено специалистов: " + data.getSpecialists().size());
            System.out.println("Загружено перелетов: " + data.getFlights().size());
            
            
            FlightTimeCalculator calculator = new FlightTimeCalculator();
            FlightReport report = calculator.calculate(data);
            
            
            ReportGenerator generator = new ReportGenerator();
            String outputName = outputPath.toString().toLowerCase();
            if (outputName.endsWith(".json")) {
                generator.generateJson(report, outputPath);
            } else if (outputName.endsWith(".xml")) {
                generator.generateXml(report, outputPath);
            } else {
                System.err.println("Ошибка: выходной файл должен быть .json или .xml");
                return;
            }
            
            System.out.println("Отчет сохранен: " + outputPath);
            
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
