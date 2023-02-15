package com.cse110.team7.socialcompass.models;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "compass_houses")
public class House {
    @PrimaryKey(autoGenerate = true)
    public long id;
    private String name;
    private LatLong location;

    public House(String name, LatLong location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLong getLocation() {
        return location;
    }

    public void setLocation(LatLong location) {
        this.location = location;
    }

    public long getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return "House{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
