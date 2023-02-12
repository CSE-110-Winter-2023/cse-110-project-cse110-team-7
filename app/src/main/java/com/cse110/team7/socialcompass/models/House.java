package com.cse110.team7.socialcompass.models;

public class House {

    private String labelName;
    private Label houseLabel;
    private LatLong location;

    public House(String labelName, LatLong location) {
        this.labelName = labelName;
        this.location = location;
    }

    public House(Label houseLabel, LatLong location) {
        this.houseLabel = houseLabel;
        this.location = location;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public LatLong getLocation() {
        return location;
    }

    public void setLocation(LatLong location) {
        this.location = location;
    }

    public Label getHouseLabel() {
        return houseLabel;
    }

}
