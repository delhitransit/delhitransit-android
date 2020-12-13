package com.delhitransit.delhitransit_android.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

public class FavouriteStopsRepository {

    private final FavouriteStopsDao mDao;
    private final LiveData<List<StopsResponseData>> mAllFavouriteStops;

    public FavouriteStopsRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mDao = db.dao();
        mAllFavouriteStops = mDao.getAllFavouriteStops();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<StopsResponseData>> getAllFavouriteStops() {
        return mAllFavouriteStops;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insertAll(StopsResponseData... stop) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insertAll(stop));
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(mDao::deleteAll);
    }


}
