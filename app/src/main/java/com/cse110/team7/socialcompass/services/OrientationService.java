package com.cse110.team7.socialcompass.services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService {
    private static final OrientationService INSTANCE = new OrientationService();

    public static OrientationService getInstance() {
        return INSTANCE;
    }

    private SensorManager sensorManager;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private final MutableLiveData<Float> azimuth;

    private final SensorEventListener sensorEventListener;

    private OrientationService() {
        this.azimuth = new MutableLiveData<>();
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        accelerometerReading = sensorEvent.values;
                        updateAzimuth();
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magnetometerReading = sensorEvent.values;
                        updateAzimuth();
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    public void registerSensorEventListener() {
        if (sensorManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Sensor Manager is null when registering sensor event listener");
        }

        sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    public void unregisterSensorEventListener() {
        if (sensorManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Sensor Manager is null when unregistering sensor event listener");
        }

        sensorManager.unregisterListener(sensorEventListener);
    }

    private void updateAzimuth() {
        if (accelerometerReading == null || magnetometerReading == null) return;

        float[] rMatrix = new float[9];
        float[] iMatrix = new float[9];

        boolean success = SensorManager.getRotationMatrix(rMatrix, iMatrix, accelerometerReading, magnetometerReading);

        if (!success) return;

        float[] orientation = new float[3];
        SensorManager.getOrientation(rMatrix, orientation);

        azimuth.postValue(orientation[0]);
    }

    public void setSensorManager(@NonNull SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth.postValue(azimuth);
    }

    public LiveData<Float> getAzimuth() {
        return azimuth;
    }
}
