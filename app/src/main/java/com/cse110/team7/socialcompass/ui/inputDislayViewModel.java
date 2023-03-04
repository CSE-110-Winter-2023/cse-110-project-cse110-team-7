package com.cse110.team7.socialcompass.ui;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LatLongConverter;
import com.cse110.team7.socialcompass.models.FriendAccount;

import java.util.List;

/**
 * This ties the display for Main Activity to the database, allowing us to access it and
 * update it, while also updating the Recycle Adapter in inputDisplayAdapter.
 */
public class inputDislayViewModel extends AndroidViewModel {
    private LiveData<List<FriendAccount>> allHouses; //Parallel list of houses.
    private final FriendAccountDao friendAccountDao; //The database

    public void addHouse(FriendAccount newFriendAccount){
        friendAccountDao.insertFriend(newFriendAccount);
    }

    public void updateLabelText(FriendAccount currFriendAccount, String labelText) {
        currFriendAccount.setName(labelText);
        friendAccountDao.updateFriend(currFriendAccount);
    }

    public void updateCoordinateText(FriendAccount currFriendAccount, String coordinateText) {
        if(coordinateText == null || coordinateText.equals("")){
            currFriendAccount.setLocation(null);
            friendAccountDao.updateFriend(currFriendAccount);
            return;
        }
        currFriendAccount.setLocation(LatLongConverter.stringToLatLong(coordinateText));
        friendAccountDao.updateFriend(currFriendAccount);
    }

    public inputDislayViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        FriendDatabase houseDao = FriendDatabase.getInstance(context);
        this.friendAccountDao = houseDao.getFriendDao();
    }

    public LiveData<List<FriendAccount>> getHouseItems() {
        if(allHouses == null) {
            loadUsers();
        }

        return allHouses;
    }

    private void loadUsers() {
        allHouses = friendAccountDao.selectFriends();
    }
}
