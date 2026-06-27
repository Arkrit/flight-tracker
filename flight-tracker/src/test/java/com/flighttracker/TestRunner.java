package com.flighttracker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    
    private static int passed = 0;
    private static int failed = 0;
    private static List<String> failures = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("=== FLIGHT TRACKER TESTS ===\n");
        
        runTests(JSONParserTest.class);
        runTests(XMLParserTest.class);
        runTests(FlightTimeCalculatorTest.class);
        runTests(ReportGeneratorTest.class);
        runTests(FullPipelineTest.class);
        
        System.out.println("===========================");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        
        if (failed > 0) {
            System.out.println("\nFailures:");
            for (String f : failures) {
                System.out.println("  " + f);
            }
            System.exit(1);
        }
    }
    
    private static void runTests(Class<?> testClass) {
        System.out.println("Running: " + testClass.getSimpleName());
        
        try {
            Object instance = testClass.getDeclaredConstructor().newInstance();
            
            
            Method[] methods = testClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("test") && method.getParameterCount() == 0) {
                    try {
                        
                        try {
                            Method setup = testClass.getDeclaredMethod("setUp");
                            setup.invoke(instance);
                        } catch (NoSuchMethodException e) {
                            
                        }
                        
                        
                        method.invoke(instance);
                        passed++;
                        System.out.println("  OK " + method.getName());
                        
                    } catch (Exception e) {
                        failed++;
                        Throwable cause = e.getCause();
                        if (cause == null) cause = e;
                        String msg = cause.getClass().getSimpleName() + ": " + cause.getMessage();
                        failures.add(testClass.getSimpleName() + "." + method.getName() + " - " + msg);
                        System.out.println("  FAIL " + method.getName() + " - " + msg);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("  FAIL Cannot create test class: " + e.getMessage());
            failed++;
        }
        
        System.out.println();
    }
    
    
    public static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("Expected [" + expected + "] but got [" + actual + "]");
        }
    }
    
    public static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected [" + expected + "] but got [" + actual + "]");
        }
    }
    
    public static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected [" + expected + "] but got [" + actual + "]");
        }
    }
    
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but got false");
        }
    }
    
    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but got true");
        }
    }
    
    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Object is null");
        }
    }
    
    public static void fail(String message) {
        throw new AssertionError(message);
    }
}

class AssertionError extends RuntimeException {
    public AssertionError(String message) {
        super(message);
    }
}