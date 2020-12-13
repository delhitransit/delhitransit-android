package com.delhitransit.delhitransit_android.fragment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.delhitransit.delhitransit_android.database.FavouriteStopsRepository;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;
import java.util.Random;

public class FavouriteStopsViewModel extends AndroidViewModel {
    private final LiveData<List<StopsResponseData>> mAllFavouriteStops;
    private final FavouriteStopsRepository mRepository;

    public FavouriteStopsViewModel(Application application) {
        super(application);
        mRepository = new FavouriteStopsRepository(application);
        mAllFavouriteStops = mRepository.getAllFavouriteStops();
    }

    public LiveData<List<StopsResponseData>> getAll() {
        return mAllFavouriteStops;
    }

    public void setFavouriteStopsAdapter(FavouriteStopsAdapter adapter, LifecycleOwner owner) {
        this.getAll().observe(owner, adapter::submitList);
    }

    private void insertDummyStop() {
        StopsResponseData stopsResponseData = new StopsResponseData();
        stopsResponseData.setName("A name for a stop");
        stopsResponseData.setStopId(new Random().nextInt());
        this.insertAll(stopsResponseData);
    }

    public void insertAll(StopsResponseData... stop) {
        mRepository.insertAll(stop);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

}