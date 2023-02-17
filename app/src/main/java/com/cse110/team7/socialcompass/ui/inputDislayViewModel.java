package com.cse110.team7.socialcompass.ui;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.backend.LatLongConverter;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

import java.util.List;

public class inputDislayViewModel extends AndroidViewModel {
    private LiveData<List<House>> allHouses;
    private final HouseDao houseDao;

    public void addHouse(House newHouse){
        houseDao.insertHouse(newHouse);
    }

    public void updateLabelText(House currHouse, String labelText) {
        currHouse.setName(labelText);
        houseDao.updateHouse(currHouse);
    }

    public void updateCoordinateText(House currHouse, String coordinateText) {
        currHouse.setLocation(LatLongConverter.stringToLatLong(coordinateText));
        houseDao.updateHouse(currHouse);
    }

    public inputDislayViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        HouseDatabase houseDao = HouseDatabase.getInstance(context);
        this.houseDao = houseDao.getHouseDao();
    }

    public LiveData<List<House>> getHouseItems() {
        if(allHouses == null) {
            loadUsers();
        }

        return allHouses;
    }

    private void loadUsers() {
        allHouses = houseDao.selectHouses();
    }
}
