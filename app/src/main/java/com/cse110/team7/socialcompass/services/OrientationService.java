package com.cse110.team7.socialcompass.services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService {
    /**
     * this field is the only available instance of this class for the
     * entire lifecycle of the application, access it by the getter function
     */
    private static final OrientationService INSTANCE = new OrientationService();

    /**
     * Get the instance of the class
     *
     * @return the only available instance of this class
     */
    public static OrientationService getInstance() {
        return INSTANCE;
    }

    /**
     * this field is an active system sensor manager,
     * which will be obtained by an active activity elsewhere
     */
    private SensorManager sensorManager;
    private WindowManager windowManager;
    /**
     * this field stores the current readings from the
     * accelerometer sensor of the phone
     */
    private float[] accelerometerReading;
    /**
     * this field stores the current readings from the
     * magnetometer sensor of the phone
     */
    private float[] magnetometerReading;
    /**
     * this field take cares of the lifecycle, observers
     * will only be called when the activity it belongs is active
     */
    private final MutableLiveData<Float> azimuth;
    /**
     * this field is a listener instance, used to remove
     * listeners from system service, will be called when a
     * new orientation value is available
     */
    private final SensorEventListener sensorEventListener;

    private OrientationService() {
        this.azimuth = new MutableLiveData<>();
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    // we only care about these two types of sensor events
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

    /**
     * Call this method to start receiving orientation updates
     */
    public void registerSensorEventListener() {
        if (sensorManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Sensor Manager is null when registering sensor event listener");
        }

        // register listeners for both of the sensors
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

    /**
     * Call this method to stop receiving location updates from system location services
     */
    public void unregisterSensorEventListener() {
        if (sensorManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Sensor Manager is null when unregistering sensor event listener");
        }

        sensorManager.unregisterListener(sensorEventListener);
    }

    /**
     * Calculate the current azimuth (user orientation) from sensor readings
     *
     * @see <a href="https://docs.google.com/document/d/1eJXPdQ44Y50qMxh6Npg4gkJBfi3Gs4cvGc2My_yWkDw">Demo 5</a>
     */
    private void updateAzimuth() {
        if (accelerometerReading == null || magnetometerReading == null) return;

        float[] rMatrix = new float[9];
        float[] iMatrix = new float[9];

        boolean success = SensorManager.getRotationMatrix(rMatrix, iMatrix, accelerometerReading, magnetometerReading);

        if (!success) return;

        float[] orientation = new float[3];
        SensorManager.getOrientation(rMatrix, orientation);

        // update the azimuth value, note that orientation is -π to π, so we add 2π to it and mod
        // 2π to get the value from 0 to 2π, it is also transformed from radians into degrees
        if (windowManager != null) {
            switch (windowManager.getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    azimuth.postValue((float) Math.toDegrees((orientation[0] + 2 * Math.PI) % (2 * Math.PI)));
                    break;
                case Surface.ROTATION_90:
                    azimuth.postValue((float) Math.toDegrees((orientation[0] + 5.0 / 2 * Math.PI) % (2 * Math.PI)));
                    break;
                case Surface.ROTATION_180:
                    azimuth.postValue((float) Math.toDegrees((orientation[0] + 3 * Math.PI) % (2 * Math.PI)));
                    break;
                case Surface.ROTATION_270:
                    azimuth.postValue((float) Math.toDegrees((orientation[0] + 7.0 / 2 * Math.PI) % (2 * Math.PI)));
                    break;
            }
        } else {
            azimuth.postValue((float) Math.toDegrees((orientation[0] + 2 * Math.PI) % (2 * Math.PI)));
        }
    }

    /**
     * Set the system sensor service, must be called before registering for orientation updates
     *
     * @param sensorManager System sensor service obtained from active activity
     */
    public void setSensorManager(@NonNull SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void setWindowManager(@NonNull WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * Directly set the current orientation and trigger update immediately, this
     * can be used for testing
     *
     * @param azimuth the new orientation
     */
    public void setAzimuth(float azimuth) {
        this.azimuth.setValue(azimuth);
    }

    /**
     * Get the live data instance to add observers on it
     *
     * @return the live data instance holding the current orientation
     */
    public LiveData<Float> getAzimuth() {
        return azimuth;
    }

}
