package com.cse110.team7.socialcompass.models;

public class House {

    private String name;
    private LatLong location;

    public House(String name, LatLong location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLong getLocation() {
        return location;
    }

    public void setLocation(LatLong location) {
        this.location = location;
    }
}
