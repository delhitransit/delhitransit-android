package com.delhitransit.delhitransit_android.fragment.stop_details;

import android.app.Application;
import android.util.Log;

import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.database.FavouriteStopsRepository;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopDetailsViewModel extends AndroidViewModel {
    private final MutableLiveData<List<RoutesFromStopDetail>> allRoutes = new MutableLiveData<>();
    private final ApiInterface apiService = ApiClient.getApiService(getApplication().getApplicationContext());
    private final FavouriteStopsRepository mRepository;

    public StopDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FavouriteStopsRepository(application);
    }

    private void makeApiRequest(int stopId) {
        apiService.getAllRoutesByStopId(stopId, ((int) TimeConverter.getSecondsSince12AM()))
                .enqueue(new Callback<List<RoutesFromStopDetail>>() {
                    @Override
                    public void onResponse(Call<List<RoutesFromStopDetail>> call, Response<List<RoutesFromStopDetail>> response) {
                        if (response.body() != null && response.body().size() != 0)
                            allRoutes.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<RoutesFromStopDetail>> call, Throwable t) {
                        Log.e(StopDetailsViewModel.class.getSimpleName(), "API call failed at getAllRoutesByStopId from stopId : " + stopId);
                    }
                });
    }

    public MutableLiveData<List<RoutesFromStopDetail>> getAllRoutes(int stopId) {
        makeApiRequest(stopId);
        return allRoutes;
    }

    public LiveData<Boolean> isStopFavourite(int stopId) {
        return mRepository.contains(stopId);
    }

    public void addToFavourites(StopDetail stop) {
        mRepository.insertAll(stop);
    }

    public void removeFromFavourites(int stopId) {
        mRepository.deleteByStopId(stopId);
    }

}