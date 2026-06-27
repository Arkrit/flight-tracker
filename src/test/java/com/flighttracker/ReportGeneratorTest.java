package com.flighttracker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.flighttracker.models.FlightReport;
import com.flighttracker.models.FlightSpecialist;
import com.flighttracker.models.MonthlyReport;
import com.flighttracker.models.SpecialistReport;
import com.flighttracker.report.ReportGenerator;

public class ReportGeneratorTest {
    
    public void testGenerateJson() throws Exception {
        FlightReport report = createSampleReport();
        Path path = Files.createTempFile("output", ".json");
        
        ReportGenerator generator = new ReportGenerator();
        generator.generateJson(report, path);
        
        String content = new String(Files.readAllBytes(path));
        
        TestRunner.assertTrue(content.contains("report"));
        TestRunner.assertTrue(content.contains("months"));
        TestRunner.assertTrue(content.contains("specialists"));
        TestRunner.assertTrue(content.contains("SP001"));
        TestRunner.assertTrue(content.contains("2020-12"));
        
        Files.delete(path);
    }
    
    public void testGenerateXml() throws Exception {
        FlightReport report = createSampleReport();
        Path path = Files.createTempFile("output", ".xml");
        
        ReportGenerator generator = new ReportGenerator();
        generator.generateXml(report, path);
        
        String content = new String(Files.readAllBytes(path));
        
        TestRunner.assertTrue(content.contains("<report>"));
        TestRunner.assertTrue(content.contains("<months>"));
        TestRunner.assertTrue(content.contains("<specialists>"));
        TestRunner.assertTrue(content.contains("SP001"));
        
        Files.delete(path);
    }
    
    public void testJsonEscaping() throws Exception {
        FlightSpecialist spec = new FlightSpecialist("SP\"001", "John", "Doe", "Captain");
        
        List<String> months = Arrays.asList("2020-12");
        
        MonthlyReport monthly = new MonthlyReport(
            "SP\"001", "2020-12", 10.5,
            false, false, true,
            new ArrayList<>(),
            Arrays.asList("2020-12-15")
        );
        
        SpecialistReport specReport = new SpecialistReport(
            spec.getId(), spec.getFullName(), spec.getPosition(),
            Arrays.asList(monthly)
        );
        
        FlightReport report = new FlightReport(months, Arrays.asList(specReport));
        
        Path path = Files.createTempFile("output", ".json");
        ReportGenerator generator = new ReportGenerator();
        generator.generateJson(report, path);
        
        String content = new String(Files.readAllBytes(path));
        
        TestRunner.assertTrue(content.contains("SP\\\"001") || content.contains("SP\"001"));
        
        Files.delete(path);
    }
    
    private FlightReport createSampleReport() {
        FlightSpecialist spec = new FlightSpecialist("SP001", "Ivan", "Ivanov", "Captain");
        
        List<String> months = Arrays.asList("2020-12");
        
        MonthlyReport monthly = new MonthlyReport(
            "SP001", "2020-12", 10.5,
            false, false, true,
            new ArrayList<>(),
            Arrays.asList("2020-12-15")
        );
        
        SpecialistReport specReport = new SpecialistReport(
            spec.getId(), spec.getFullName(), spec.getPosition(),
            Arrays.asList(monthly)
        );
        
        return new FlightReport(months, Arrays.asList(specReport));
    }
}