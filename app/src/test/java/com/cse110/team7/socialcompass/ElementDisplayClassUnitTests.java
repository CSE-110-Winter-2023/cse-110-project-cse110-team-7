package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.ElementDisplay;

import org.junit.Test;

public class ElementDisplayClassUnitTests {
    @Test
    public void testConstructor() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        House testHouse = new House(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        ElementDisplay testElementDisplay = new ElementDisplay(testHouse, testImageView, testTextView);

        assertEquals(testElementDisplay.getHouse(), testHouse);
        assertEquals(testElementDisplay.getDotView(), testImageView);
        assertEquals(testElementDisplay.getLabelView(), testTextView);
        assertEquals(Double.compare(testElementDisplay.getBearing(), 0), 0);
    }

    @Test
    public void testSetDotView() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        House testHouse = new House(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        ElementDisplay testElementDisplay = new ElementDisplay(testHouse, testImageView, testTextView);

        ImageView newImageView = new ImageView(null);
        newImageView.setVisibility(ImageView.INVISIBLE);

        testElementDisplay.setDotView(newImageView);

        assertEquals(testElementDisplay.getDotView(), newImageView);
    }

    @Test
    public void testSetLabelView() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        House testHouse = new House(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        ElementDisplay testElementDisplay = new ElementDisplay(testHouse, testImageView, testTextView);

        TextView newTextView = new TextView(null);
        newTextView.setVisibility(TextView.INVISIBLE);

        testElementDisplay.setLabelView(newTextView);

        assertEquals(testElementDisplay.getLabelView(), newTextView);
    }

    @Test
    public void testUpdateBearing() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        House testHouse = new House(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        ElementDisplay testElementDisplay = new ElementDisplay(testHouse, testImageView, testTextView);

        float newBearing = 45.7f;

        testElementDisplay.updateBearing(newBearing);

        assertEquals(Double.compare(testElementDisplay.getBearing(), newBearing), 0);
    }
}
