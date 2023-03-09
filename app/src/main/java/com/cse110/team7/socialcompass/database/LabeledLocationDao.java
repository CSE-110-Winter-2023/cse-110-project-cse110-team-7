package com.cse110.team7.socialcompass.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import com.cse110.team7.socialcompass.models.LabeledLocation;

import java.util.List;

/**
 * Data access object for LabeledLocation
 */
@Dao
public abstract class LabeledLocationDao {
    @Upsert
    public abstract long upsertLabeledLocation(LabeledLocation labeledLocation);

    @Query("SELECT EXISTS(SELECT 1 FROM `labeled_locations` WHERE publicCode = :publicCode)")
    public abstract boolean isLabeledLocationExists(String publicCode);

    @Query("SELECT * FROM `labeled_locations` WHERE publicCode = :publicCode")
    public abstract LabeledLocation selectLabeledLocationWithoutLiveData(String publicCode);

    @Query("SELECT * FROM `labeled_locations` WHERE publicCode = :publicCode")
    public abstract LiveData<LabeledLocation> selectLabeledLocation(String publicCode);

    @Query("SELECT * FROM `labeled_locations` ORDER BY createdAt")
    public abstract LiveData<List<LabeledLocation>> selectLabeledLocations();

    @Delete
    public abstract int deleteLabeledLocation(LabeledLocation labeledLocation);
}
