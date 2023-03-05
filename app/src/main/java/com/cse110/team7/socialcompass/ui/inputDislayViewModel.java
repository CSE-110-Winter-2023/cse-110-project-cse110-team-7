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
    private LiveData<List<FriendAccount>> allFriends; //Parallel list of friends.
    private final FriendAccountDao friendAccountDao; //The database

    public void addFriend(FriendAccount newFriendAccount){
        friendAccountDao.insertFriend(newFriendAccount); //Only adds if not already in database for now.
    }

    public FriendAccountDao getDb(){
        return friendAccountDao;
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
        FriendDatabase friendDao = FriendDatabase.getInstance(context);
        this.friendAccountDao = friendDao.getFriendDao();
    }

    public LiveData<List<FriendAccount>> getFriendItems() {
        if(allFriends == null) {
            loadUsers();
        }

        return allFriends;
    }

    private void loadUsers() {
        allFriends = friendAccountDao.selectFriends();
    }
}
