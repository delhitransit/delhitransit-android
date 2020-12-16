package com.delhitransit.delhitransit_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {StopDetail.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;

    }

    public abstract FavouriteStopsDao dao();

}
