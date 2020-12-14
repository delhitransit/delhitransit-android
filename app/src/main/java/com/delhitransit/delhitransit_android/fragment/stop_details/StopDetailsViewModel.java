package com.delhitransit.delhitransit_android.fragment.stop_details;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopDetailsViewModel extends AndroidViewModel {
    private final MutableLiveData<List<RoutesFromStopDetail>> allRoutes = new MutableLiveData<>();
    private final ApiInterface apiService = ApiClient.getApiService(getApplication().getApplicationContext());

    public StopDetailsViewModel(@NonNull Application application) {
        super(application);
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

}