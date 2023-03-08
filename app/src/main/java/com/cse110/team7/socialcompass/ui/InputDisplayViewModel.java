package com.cse110.team7.socialcompass.ui;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendAccountRepository;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LatLongConverter;
import com.cse110.team7.socialcompass.models.FriendAccount;

import java.util.List;

/**
 * This ties the display for Main Activity to the database, allowing us to access it and
 * update it, while also updating the Recycle Adapter in inputDisplayAdapter.
 */
public class InputDisplayViewModel extends AndroidViewModel {
    private LiveData<List<FriendAccount>> allFriends; //Parallel list of friends.
    private final FriendAccountDao friendAccountDao; //The database

    private final FriendAccountRepository friendAccountRepository;

    public InputDisplayViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        FriendDatabase friendDao = FriendDatabase.getInstance(context);
        this.friendAccountDao = friendDao.getFriendDao();
        this.friendAccountRepository = new FriendAccountRepository(this.friendAccountDao);
    }

    public void addFriend(FriendAccount newFriendAccount){
        friendAccountRepository.syncedUpsert(newFriendAccount); //Only adds if not already in database for now.
    }

    public LiveData<List<FriendAccount>> getFriendItems() {
        if(allFriends == null) {
            loadUsers();
        }

        return allFriends;
    }

    private void loadUsers() {
        allFriends = friendAccountRepository.selectLocalFriendAccounts();
    }

    public FriendAccountDao getDb(){
        return friendAccountDao;
    }

    public FriendAccountRepository getRepository() {
        return friendAccountRepository;
    }



    // Deprecated for MS2
    public void updateLabelText(FriendAccount currFriendAccount, String labelText) {
        currFriendAccount.setName(labelText);
        friendAccountDao.updateFriend(currFriendAccount);
    }

    // Deprecated for MS2
    public void updateCoordinateText(FriendAccount currFriendAccount, String coordinateText) {
        if(coordinateText == null || coordinateText.equals("")){
            currFriendAccount.setLocation(null);
            friendAccountDao.updateFriend(currFriendAccount);
            return;
        }
        currFriendAccount.setLocation(LatLongConverter.stringToLatLong(coordinateText));
        friendAccountDao.updateFriend(currFriendAccount);
    }

}
