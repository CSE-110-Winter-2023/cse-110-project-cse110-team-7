package com.cse110.team7.socialcompass.services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;


/**
 * Observes orientation updates from user and broadcast to all observers
 */
public class OrientationService {
    private static OrientationService INSTANCE = null;

    private final MutableLiveData<Double> currentOrientation;
    private final SensorEventListener sensorEventUpdateListener;
    private SensorManager sensorManager;
    private WindowManager windowManager;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private boolean isListenerRegistered;

    private void updateCurrentOrientation() {
        if (accelerometerReading == null || magnetometerReading == null) {
            Log.d(OrientationService.class.getName(), "readings not initialized");
            return;
        }

        float[] rMatrix = new float[9];
        float[] iMatrix = new float[9];

        boolean success = SensorManager.getRotationMatrix(rMatrix, iMatrix, accelerometerReading, magnetometerReading);

        if (!success) {
            Log.w(OrientationService.class.getName(), "get rotation matrix failed");
            return;
        }

        float[] orientation = new float[3];
        SensorManager.getOrientation(rMatrix, orientation);

        double zOrientation = Math.toDegrees((orientation[0] + 2 * Math.PI) % (2 * Math.PI));

        switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                zOrientation += 0;
                break;
            case Surface.ROTATION_90:
                zOrientation += 90;
                break;
            case Surface.ROTATION_180:
                zOrientation += 180;
                break;
            case Surface.ROTATION_270:
                zOrientation += 270;
                break;
        }

        if (currentOrientation.getValue() == null || Double.compare(zOrientation, currentOrientation.getValue()) != 0) {
            Log.d(OrientationService.class.getName(), "update current orientation to " + zOrientation);

            currentOrientation.postValue(zOrientation);
        }
    }

    private OrientationService() {
        this.currentOrientation = new MutableLiveData<>();
        this.sensorEventUpdateListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        accelerometerReading = event.values;
                        updateCurrentOrientation();
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magnetometerReading = event.values;
                        updateCurrentOrientation();
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        this.isListenerRegistered = false;
    }

    public static OrientationService getInstance() {
        if (INSTANCE == null) INSTANCE = new OrientationService();
        return INSTANCE;
    }

    /**
     * Register the sensor event update listener to start receiving orientation updates from sensor
     */
    public void registerSensorEventUpdateListener() {
        if (isListenerRegistered) {
            Log.w(OrientationService.class.getName(), "sensor event update listener is already registered when registering sensor event update listener");
            return;
        }

        if (sensorManager == null) {
            Log.w(OrientationService.class.getName(), "sensor manager is null when registering sensor event update listener");
            return;
        }

        if (windowManager == null) {
            Log.w(OrientationService.class.getName(), "window manager is null when registering sensor event update listener");
            return;
        }

        sensorManager.registerListener(
                sensorEventUpdateListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        sensorManager.registerListener(
                sensorEventUpdateListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        isListenerRegistered = true;

        Log.i(OrientationService.class.getName(), "sensor event update listener registered");
    }

    /**
     * Unregister the sensor event update listener to stop receiving orientation updates from sensor
     */
    public void unregisterSensorEventUpdateListener() {
        if (!isListenerRegistered) {
            Log.w(OrientationService.class.getName(), "sensor event update listener is not registered when unregistering sensor event update listener");
            return;
        }

        if (sensorManager == null) {
            Log.w(OrientationService.class.getName(), "sensor manager is null when unregistering sensor event update listener");
            return;
        }

        sensorManager.unregisterListener(sensorEventUpdateListener);

        isListenerRegistered = false;

        Log.i(OrientationService.class.getName(), "sensor event update listener unregistered");
    }

    /**
     * Broadcast the given orientation immediately
     *
     * @param orientation the orientation to be sent to all observers
     */
    public void setCurrentOrientation(double orientation) {
        this.currentOrientation.setValue(orientation);
    }

    /**
     * Get the orientation subject
     *
     * @return the orientation subject
     */
    public MutableLiveData<Double> getCurrentOrientation() {
        return currentOrientation;
    }

    public void setSensorManager(@NonNull SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void setWindowManager(@NonNull WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    @VisibleForTesting
    public static void clearOrientationService() {
        INSTANCE = null;
    }
}
