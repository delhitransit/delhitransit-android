package com.delhitransit.delhitransit_android.fragment.favourite_stops;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.delhitransit.delhitransit_android.database.FavouriteStopsRepository;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.List;

public class FavouriteStopsViewModel extends AndroidViewModel {
    private final LiveData<List<StopDetail>> mAllFavouriteStops;
    private final FavouriteStopsRepository mRepository;

    public FavouriteStopsViewModel(Application application) {
        super(application);
        mRepository = new FavouriteStopsRepository(application);
        mAllFavouriteStops = mRepository.getAllFavouriteStops();
    }

    public LiveData<List<StopDetail>> getAll() {
        return mAllFavouriteStops;
    }

    public void insertAll(StopDetail... stop) {
        mRepository.insertAll(stop);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteByStopId(int stopId) {
        mRepository.deleteByStopId(stopId);
    }

}