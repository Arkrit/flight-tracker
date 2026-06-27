package com.flighttracker.models;

public class FlightSpecialist {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String position;

    public FlightSpecialist(String id, String firstName, String lastName, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPosition() { return position; }
    public String getFullName() { return firstName + " " + lastName; }
}