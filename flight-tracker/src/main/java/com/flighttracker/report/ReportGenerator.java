package com.flighttracker.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.flighttracker.models.FlightReport;
import com.flighttracker.models.MonthlyReport;
import com.flighttracker.models.SpecialistReport;

public class ReportGenerator {

    public void generateJson(FlightReport report, Path outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("{\n");
        sb.append("  \"report\": {\n");
        
        
        sb.append("    \"months\": [");
        List<String> months = report.getMonths();
        for (int i = 0; i < months.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(months.get(i)).append("\"");
        }
        sb.append("],\n");
        
        
        sb.append("    \"specialists\": [\n");
        List<SpecialistReport> specs = report.getSpecialists();
        for (int i = 0; i < specs.size(); i++) {
            if (i > 0) sb.append(",\n");
            sb.append("      ");
            writeSpecialistJson(sb, specs.get(i));
        }
        sb.append("\n    ]\n");
        
        sb.append("  }\n");
        sb.append("}\n");
        
        Files.write(outputPath, sb.toString().getBytes());
    }
    
    private void writeSpecialistJson(StringBuilder sb, SpecialistReport spec) {
        sb.append("{\n");
        sb.append("        \"id\": \"").append(escapeJson(spec.getId())).append("\",\n");
        sb.append("        \"name\": \"").append(escapeJson(spec.getName())).append("\",\n");
        sb.append("        \"position\": \"").append(escapeJson(spec.getPosition())).append("\",\n");
        sb.append("        \"monthlyReports\": [\n");
        
        List<MonthlyReport> reports = spec.getMonthlyReports();
        for (int i = 0; i < reports.size(); i++) {
            if (i > 0) sb.append(",\n");
            sb.append("          ");
            writeMonthlyJson(sb, reports.get(i));
        }
        
        sb.append("\n        ]\n");
        sb.append("      }");
    }
    
    private void writeMonthlyJson(StringBuilder sb, MonthlyReport report) {
        sb.append("{\n");
        sb.append("            \"month\": \"").append(report.getMonth()).append("\",\n");
        sb.append("            \"totalHours\": ").append(report.getTotalHours()).append(",\n");
        sb.append("            \"flags\": {\n");
        sb.append("              \"exceeded80HoursPerMonth\": ").append(report.isExceeded80Hours()).append(",\n");
        sb.append("              \"exceeded36HoursPerWeek\": ").append(report.isExceeded36HoursPerWeek()).append(",\n");
        sb.append("              \"exceeded8HoursPerDay\": ").append(report.isExceeded8HoursPerDay()).append("\n");
        sb.append("            },\n");
        sb.append("            \"details\": {\n");
        
        
        sb.append("              \"daysExceeded8Hours\": [");
        List<String> days = report.getDaysExceeded8Hours();
        for (int i = 0; i < days.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(days.get(i)).append("\"");
        }
        sb.append("],\n");
        
        
        sb.append("              \"weeksExceeded36Hours\": [");
        List<String> weeks = report.getWeeksExceeded36Hours();
        for (int i = 0; i < weeks.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(weeks.get(i)).append("\"");
        }
        sb.append("]\n");
        
        sb.append("            }\n");
        sb.append("          }");
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
    
    
    
    public void generateXml(FlightReport report, Path outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<report>\n");
        
        
        sb.append("  <months>\n");
        for (String month : report.getMonths()) {
            sb.append("    <month>").append(month).append("</month>\n");
        }
        sb.append("  </months>\n");
        
        
        sb.append("  <specialists>\n");
        for (SpecialistReport spec : report.getSpecialists()) {
            writeSpecialistXml(sb, spec);
        }
        sb.append("  </specialists>\n");
        
        sb.append("</report>\n");
        
        Files.write(outputPath, sb.toString().getBytes());
    }
    
    private void writeSpecialistXml(StringBuilder sb, SpecialistReport spec) {
        sb.append("    <specialist>\n");
        sb.append("      <id>").append(escapeXml(spec.getId())).append("</id>\n");
        sb.append("      <name>").append(escapeXml(spec.getName())).append("</name>\n");
        sb.append("      <position>").append(escapeXml(spec.getPosition())).append("</position>\n");
        sb.append("      <monthlyReports>\n");
        
        for (MonthlyReport report : spec.getMonthlyReports()) {
            writeMonthlyXml(sb, report);
        }
        
        sb.append("      </monthlyReports>\n");
        sb.append("    </specialist>\n");
    }
    
    private void writeMonthlyXml(StringBuilder sb, MonthlyReport report) {
        sb.append("        <monthlyReport>\n");
        sb.append("          <month>").append(report.getMonth()).append("</month>\n");
        sb.append("          <totalHours>").append(report.getTotalHours()).append("</totalHours>\n");
        sb.append("          <flags>\n");
        sb.append("            <exceeded80HoursPerMonth>").append(report.isExceeded80Hours()).append("</exceeded80HoursPerMonth>\n");
        sb.append("            <exceeded36HoursPerWeek>").append(report.isExceeded36HoursPerWeek()).append("</exceeded36HoursPerWeek>\n");
        sb.append("            <exceeded8HoursPerDay>").append(report.isExceeded8HoursPerDay()).append("</exceeded8HoursPerDay>\n");
        sb.append("          </flags>\n");
        sb.append("          <details>\n");
        
        sb.append("            <daysExceeded8Hours>\n");
        for (String day : report.getDaysExceeded8Hours()) {
            sb.append("              <day>").append(day).append("</day>\n");
        }
        sb.append("            </daysExceeded8Hours>\n");
        
        sb.append("            <weeksExceeded36Hours>\n");
        for (String week : report.getWeeksExceeded36Hours()) {
            sb.append("              <week>").append(week).append("</week>\n");
        }
        sb.append("            </weeksExceeded36Hours>\n");
        
        sb.append("          </details>\n");
        sb.append("        </monthlyReport>\n");
    }
    
    private String escapeXml(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}