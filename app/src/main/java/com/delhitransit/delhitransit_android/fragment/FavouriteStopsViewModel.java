package com.delhitransit.delhitransit_android.fragment;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.delhitransit.delhitransit_android.database.FavouriteStopsRepository;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

public class FavouriteStopsViewModel extends ViewModel {
    private final LiveData<List<StopsResponseData>> mAllFavouriteStops;
    private final FavouriteStopsRepository mRepository;

    public FavouriteStopsViewModel(Application application) {
        mRepository = new FavouriteStopsRepository(application);
        mAllFavouriteStops = mRepository.getAllFavouriteStops();
    }

    public LiveData<List<StopsResponseData>> getAll() {
        return mAllFavouriteStops;
    }

    public void insertAll(StopsResponseData... stop) {
        mRepository.insertAll(stop);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

}