package com.cse110.team7.socialcompass.backend;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cse110.team7.socialcompass.models.House;

import java.util.List;

@Dao
public interface HouseDao {
    @Insert
    long insertHouse(House house);

    @Insert
    List<Long> insertHouses(List<House> houses);

    @Query("SELECT * FROM `compass_houses` WHERE `id`=:id")
    House selectHouse(long id);

    @Query("SELECT * FROM `compass_houses` ORDER BY `id`")
    LiveData<List<House>> selectHouses();

    @Update
    int updateHouse(House house);

    @Delete
    int deleteHouse(House house);
}
