package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.LabelInformation;

import org.junit.Test;

public class LabelInformationClassUnitTests {
    @Test
    public void testConstructor() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        FriendAccount testFriendAccount = new FriendAccount(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        LabelInformation testLabelInformation = new LabelInformation(testFriendAccount, testImageView, testTextView);

        assertEquals(testLabelInformation.getFriend(), testFriendAccount);
        assertEquals(testLabelInformation.getDotView(), testImageView);
        assertEquals(testLabelInformation.getLabelView(), testTextView);
        assertEquals(Double.compare(testLabelInformation.getBearing(), 0), 0);
    }

    @Test
    public void testSetDotView() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        FriendAccount testFriendAccount = new FriendAccount(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        LabelInformation testLabelInformation = new LabelInformation(testFriendAccount, testImageView, testTextView);

        ImageView newImageView = new ImageView(null);
        newImageView.setVisibility(ImageView.INVISIBLE);

        testLabelInformation.setDotView(newImageView);

        assertEquals(testLabelInformation.getDotView(), newImageView);
    }

    @Test
    public void testSetLabelView() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        FriendAccount testFriendAccount = new FriendAccount(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        LabelInformation testLabelInformation = new LabelInformation(testFriendAccount, testImageView, testTextView);

        TextView newTextView = new TextView(null);
        newTextView.setVisibility(TextView.INVISIBLE);

        testLabelInformation.setLabelView(newTextView);

        assertEquals(testLabelInformation.getLabelView(), newTextView);
    }

    @Test
    public void testUpdateBearing() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        FriendAccount testFriendAccount = new FriendAccount(testName, testLocation);
        ImageView testImageView = new ImageView(null);
        TextView testTextView = new TextView(null);

        LabelInformation testLabelInformation = new LabelInformation(testFriendAccount, testImageView, testTextView);

        float newBearing = 45.7f;

        testLabelInformation.updateBearing(newBearing);

        assertEquals(Double.compare(testLabelInformation.getBearing(), newBearing), 0);
    }
}
