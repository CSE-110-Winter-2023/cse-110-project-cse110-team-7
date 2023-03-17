package com.cse110.team7.socialcompass.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cse110.team7.socialcompass.models.LabeledLocation;


/**
 * Database for the application
 */
@Database(entities = {LabeledLocation.class}, version = 1, exportSchema = false)
public abstract class SocialCompassDatabase extends RoomDatabase {
    private volatile static SocialCompassDatabase INSTANCE = null;
    private static final String DATABASE_NAME = "social_compass.db";

    /**
     * Create a database instance for the provided context
     *
     * @param context the context which the database instance will live on
     * @return a new database instance for the provided context
     */
    private static SocialCompassDatabase create(@NonNull Context context) {
        return Room.databaseBuilder(context, SocialCompassDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    /**
     * Get a singleton database instance
     *
     * @param context the context which the database instance will live on
     * @return the singleton database instance
     */
    public synchronized static SocialCompassDatabase getInstance(@NonNull Context context) {
        if (INSTANCE == null) INSTANCE = create(context);
        return INSTANCE;
    }

    /**
     * Set the singleton database instance to the given database
     * @param testDatabase the test database to be injected
     */
    @VisibleForTesting
    public static void injectTestDatabase(SocialCompassDatabase testDatabase) {
        if (INSTANCE != null) INSTANCE.close();
        INSTANCE = testDatabase;
    }

    /**
     * Get the DAO for LabeledLocation
     * @return the DAO for LabeledLocation
     */
    public abstract LabeledLocationDao getLabeledLocationDao();
}