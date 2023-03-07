package com.cse110.team7.socialcompass.backend;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FriendAccountRepository {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private final FriendAccountDao dao;
    private final Map<String, MutableLiveData<FriendAccount>> cache;
    private final Map<String, ScheduledFuture<?>> scheduledUpdate;

    public FriendAccountRepository(FriendAccountDao dao) {
        this.dao = dao;
        this.cache = new HashMap<>();
        this.scheduledUpdate = new HashMap<>();
    }

    public LiveData<FriendAccount> selectLocalFriendAccount(String publicID) {
        return dao.selectFriendLive(publicID.hashCode());
    }

    public LiveData<List<FriendAccount>> selectLocalFriendAccounts() {
        return dao.selectFriends();
    }

    public void insertLocalFriendAccount(@NonNull FriendAccount friendAccount) {
        friendAccount.setUpdatedAt(Instant.now().getEpochSecond());
        dao.insertFriend(friendAccount);
    }

    public void updateLocalFriendAccount(@NonNull FriendAccount friendAccount) {
        friendAccount.setUpdatedAt(Instant.now().getEpochSecond());
        dao.updateFriend(friendAccount);
    }

    public void upsertLocalFriendAccount(@NonNull FriendAccount friendAccount) {
        friendAccount.setUpdatedAt(Instant.now().getEpochSecond());
        dao.upsertFriend(friendAccount);
    }

    public void deleteLocalFriendAccount(@NonNull FriendAccount friendAccount) {
        dao.deleteFriend(friendAccount);
    }

    public boolean isFriendAccountExists(@NonNull String publicID) {
        return dao.isFriendAccountExists(publicID.hashCode());
    }

    public LiveData<FriendAccount> selectRemoteFriendAccount(String publicID) {
        var friendAccountLiveData = cache.getOrDefault(publicID, null);

        if (friendAccountLiveData != null) return friendAccountLiveData;

        FriendAccount friendAccount = null;

        try {
            friendAccount = LocationAPI.provide().getFriendAsync(publicID).get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (friendAccount == null) {
            friendAccount = new FriendAccount("", new LatLong(0, 0), publicID);
        }

        friendAccountLiveData = new MutableLiveData<>(friendAccount);
        cache.put(publicID, friendAccountLiveData);

        var scheduledFriendAccountUpdate = SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            var cachedFriendAccountLiveData = cache.getOrDefault(publicID, null);
            if (cachedFriendAccountLiveData == null) return;
            cachedFriendAccountLiveData.postValue(LocationAPI.provide().getFriend(publicID));
        }, 0, 3, TimeUnit.SECONDS);

        scheduledUpdate.put(publicID, scheduledFriendAccountUpdate);

        return friendAccountLiveData;
    }

    public void putRemoteFriendAccount(@NonNull FriendAccount friendAccount) {
        LocationAPI.provide().updateLocation(friendAccount);
    }

    public void deleteRemoteFriendAccount(@NonNull FriendAccount friendAccount) {
        LocationAPI.provide().deleteFriend(friendAccount);
    }

    public LiveData<FriendAccount> syncedSelectFriendAccount(@NonNull String publicID) {
        var friendAccount = new MediatorLiveData<FriendAccount>();

        Observer<FriendAccount> updateFromRemote = (remoteFriendAccount) -> {
            var currentFriendAccount = friendAccount.getValue();

            if (remoteFriendAccount == null) return;

            if (currentFriendAccount == null || currentFriendAccount.getUpdatedAt() < remoteFriendAccount.getUpdatedAt()) {
                upsertLocalFriendAccount(remoteFriendAccount);
            }
        };

        friendAccount.addSource(selectLocalFriendAccount(publicID), friendAccount::postValue);
        friendAccount.addSource(selectRemoteFriendAccount(publicID), updateFromRemote);

        return friendAccount;
    }

    public void syncedUpsert(@NonNull FriendAccount friendAccount) {
        upsertLocalFriendAccount(friendAccount);
        putRemoteFriendAccount(friendAccount);
    }

    public void syncedDelete(@NonNull FriendAccount friendAccount) {
        deleteLocalFriendAccount(friendAccount);
        deleteRemoteFriendAccount(friendAccount);

        var scheduledFriendAccountUpdate = scheduledUpdate.getOrDefault(friendAccount.getPublicID(), null);

        if (scheduledFriendAccountUpdate != null) {
            scheduledFriendAccountUpdate.cancel(false);
        }
    }
}
