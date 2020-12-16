package com.delhitransit.delhitransit_android.fragment.route_stops;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteStopsViewModel extends AndroidViewModel {
    private final ApiInterface apiService = ApiClient.getApiService(getApplication().getApplicationContext());
    private final MutableLiveData<List<StopDetail>> allStops = new MutableLiveData<>();

    public RouteStopsViewModel(@NonNull Application application) {
        super(application);
    }

    private void makeApiRequest(String tripId) {
        apiService.getStopsByTripId(tripId)
                .enqueue(new Callback<List<StopDetail>>() {
                    @Override
                    public void onResponse(Call<List<StopDetail>> call, Response<List<StopDetail>> response) {
                        if (response.body() != null && response.body().size() != 0)
                            allStops.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<StopDetail>> call, Throwable t) {
                        Log.e(RouteStopsViewModel.class.getSimpleName(), "API call failed at getStopsByTripId from tripId : " + tripId);
                    }
                });
    }

    public MutableLiveData<List<StopDetail>> getAllStops(String tripId) {
        if (tripId != null && !tripId.isEmpty()) makeApiRequest(tripId);
        return allStops;
    }
}