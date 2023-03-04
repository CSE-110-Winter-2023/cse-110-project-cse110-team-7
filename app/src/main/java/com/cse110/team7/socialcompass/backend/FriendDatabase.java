package com.cse110.team7.socialcompass.backend;


import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.cse110.team7.socialcompass.models.FriendAccount;


/**
 * exportSchema = true sounds evil, but it fixes the errors professor mentioned
 * in lab 6 that requires us to downgrade our target sdk to 31
 */

@Database(entities = {FriendAccount.class}, version = 1, exportSchema = false)
@TypeConverters({LatLongConverter.class})
public abstract class FriendDatabase extends RoomDatabase {
    private static FriendDatabase INSTANCE = null;

    public abstract FriendAccountDao getFriendDao();

    public synchronized static FriendDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = createDatabase(context);
        }

        return INSTANCE;
    }

    /**
     * Create the database instance on given context, note that we don't have addCallback
     * in this implementation because we are not populating the database with some
     * files
     *
     * @param context the context that the database instance will exist on
     * @return the generated database instance
     * @see <a href="https://docs.google.com/document/d/1OCCbewWWh3sm53xgaDjvtGxqWgL8nyV5atbJtnaImjY">Lab 6</a>
     */
    private static FriendDatabase createDatabase(Context context) {
        return Room.databaseBuilder(context, FriendDatabase.class, "compass_app.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * This function should only be used in testing
     *
     * @param friendDatabase the mock database
     */
    @VisibleForTesting
    public static void injectTestDatabase(FriendDatabase friendDatabase) {
        if (INSTANCE != null) {
            INSTANCE.close();
        }

        INSTANCE = friendDatabase;
    }
}
