package com.delhitransit.delhitransit_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

@Database(entities = {StopsResponseData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase database;

    public static AppDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, AppDatabase.class, "database-name").build();
        }
        return database;
    }

    public abstract StopResponseDataDao dao();

}
