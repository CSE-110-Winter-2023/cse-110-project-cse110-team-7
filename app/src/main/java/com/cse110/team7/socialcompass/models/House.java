package com.cse110.team7.socialcompass.models;

public class House {
    private final Label houseLabel;
    private final LatLong location;

    public House(Label houseLabel, LatLong location) {
        this.houseLabel = houseLabel;
        this.location = location;
    }

    public LatLong getLocation() {
        return location;
    }

    public Label getHouseLabel() {
        return houseLabel;
    }

    //Still need to permanently save data; probably do it here?
}
