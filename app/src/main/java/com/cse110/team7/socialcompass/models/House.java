package com.cse110.team7.socialcompass.models;

import com.cse110.team7.socialcompass.ui.ElementDisplay;

public class House {

    private ElementDisplay houseLabel;
    private LatLong location;

    public House(ElementDisplay houseLabel, LatLong location) {
        this.houseLabel = houseLabel;
        this.location = location;
    }

    public LatLong getLocation() {
        return location;
    }

    public void setLocation(LatLong location) {
        this.location = location;
    }


    public ElementDisplay getHouseDisplay() {
        return houseLabel;
    }

    public void setDisplay(ElementDisplay e) {
        houseLabel = e;
    }

}
