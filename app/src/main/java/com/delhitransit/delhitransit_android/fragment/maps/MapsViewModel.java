package com.delhitransit.delhitransit_android.fragment.maps;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.pojos.route.RouteDetailForAdapter;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsViewModel extends AndroidViewModel {

    public static final double NEARBY_STOPS_DEFAULT_DISTANCE = 1;
    private static final String TAG = MapsViewModel.class.getSimpleName();
    private final MutableLiveData<List<StopDetail>> nearbyStops = new MutableLiveData<>();
    private final ApiInterface apiService = ApiClient.getApiService(getApplication().getApplicationContext());
    private final MutableLiveData<List<RouteDetailForAdapter>> routesList = new MutableLiveData<>();
    private double userLatitude;
    private double userLongitude;

    public MapsViewModel(@NonNull Application application) {
        super(application);
    }

    private void makeNearbyStopsApiRequest(double dist) {
        if (dist >= 5) return;
        apiService.getNearByStops(dist, userLatitude, userLongitude)
                .enqueue(new Callback<List<StopDetail>>() {
                    @Override
                    public void onResponse(Call<List<StopDetail>> call, Response<List<StopDetail>> response) {
                        if (response.body() != null) {
                            if (response.body().size() > 4) {
                                nearbyStops.setValue(response.body());
                            } else {
                                makeNearbyStopsApiRequest(dist + 0.25);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<StopDetail>> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

    public MutableLiveData<List<StopDetail>> getNearbyStops() {
        List<StopDetail> list = nearbyStops.getValue();
        if (list == null || list.isEmpty()) {
            makeNearbyStopsApiRequest(NEARBY_STOPS_DEFAULT_DISTANCE);
        }
        return nearbyStops;
    }

    public void setUserCoordinates(double userLatitude, double userLongitude) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        makeNearbyStopsApiRequest(NEARBY_STOPS_DEFAULT_DISTANCE);
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public ApiInterface getApiService() {
        return apiService;
    }

    public MutableLiveData<List<RouteDetailForAdapter>> getRoutesList() {
        return routesList;
    }

    public void setRoutesList(List<RouteDetailForAdapter> list) {
        routesList.setValue(list);
    }

}
