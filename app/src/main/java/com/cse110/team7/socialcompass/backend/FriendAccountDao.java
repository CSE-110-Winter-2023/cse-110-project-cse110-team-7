package com.cse110.team7.socialcompass.backend;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cse110.team7.socialcompass.models.FriendAccount;

import java.util.List;

@Dao
public interface FriendAccountDao {
    @Insert
    long insertFriend(FriendAccount friendAccount);

    @Insert
    List<Long> insertFriends(List<FriendAccount> friendAccounts);

    @Query("SELECT * FROM 'friend_locations' WHERE `id`=:id")
    FriendAccount selectFriend(long id);

    @Query("SELECT * FROM 'friend_locations' ORDER BY `id`")
    LiveData<List<FriendAccount>> selectFriends();

    @Update
    int updateFriend(FriendAccount friendAccount);

    @Delete
    int deleteFriend(FriendAccount friendAccount);
}
