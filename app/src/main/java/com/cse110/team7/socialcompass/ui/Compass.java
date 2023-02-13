package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

import java.util.ArrayList;

public class Compass {
    ArrayList<ElementDisplay> elements;

    public Compass(ImageView northView) {
        House north = new House("North", new LatLong(90, 0));
        this.elements = new ArrayList<>();
        elements.add(new ElementDisplay(north, northView, null));
    }

    public ArrayList<ElementDisplay> getElements() {
        return elements;
    }

    public ElementDisplay getNorthElementDisplay() {
        return elements.get(0);
    }

    public void add(ElementDisplay elementDisplay) {
        elements.add(elementDisplay);
    }
}
