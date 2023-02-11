package com.cse110.team7.socialcompass.models;

import android.widget.ImageView;
import android.widget.TextView;

public class Label {
    TextView labelName;
    ImageView labelImage;

    float labelOrientation;

    public Label(TextView labelName, ImageView labelImage) {
        this.labelName = labelName;
        this.labelImage = labelImage;
    }

    public TextView getLabelName() {
        return labelName;
    }

    public ImageView getLabelImageView() {
        return labelImage;
    }

    //Might need to move this to somewhere else:
    public float getLabelOrientation() {
        return labelOrientation;
    }

    public void setLabelOrientation(float orientation) {
        this.labelOrientation = labelOrientation;
    }
}
