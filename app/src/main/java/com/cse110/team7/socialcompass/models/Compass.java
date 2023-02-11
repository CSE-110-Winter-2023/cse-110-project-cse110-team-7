package com.cse110.team7.socialcompass.models;

import android.widget.ImageView;

import java.util.ArrayList;

public class Compass {
    Label northLabel;
    ArrayList<House> allHouses;

    public Compass(Label northLabel, ArrayList<House> allHouses) {
        this.northLabel = northLabel;
        this.allHouses = allHouses;
    }

    public Label getNorthLabel() {
        return northLabel;
    }

    public ArrayList<House> getAllHomes() {
        return allHouses;
    }
}
