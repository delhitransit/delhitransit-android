package com.delhitransit.delhitransit_android.fragment.realtime_tracker;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.fragment.maps.MapsViewModel;
import com.delhitransit.delhitransit_android.pojos.RealtimeUpdate;
import com.delhitransit.delhitransit_android.pojos.route.Route;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealtimeTrackerViewModel extends AndroidViewModel {

    private static final String TAG = RealtimeTrackerViewModel.class.getSimpleName();
    public final MutableLiveData<List<RealtimeUpdate>> realtimeUpdateList = new MutableLiveData<>();
    private final ApiInterface apiService = ApiClient.getApiService(getApplication().getApplicationContext());
    private final DelhiTransitApplication applicationPreferences;
    private final Handler realtimeUpdateHandler = new Handler();

    public RealtimeTrackerViewModel(@NonNull @NotNull Application application) {
        super(application);
        applicationPreferences = getApplication();
    }

    private void fetchRealtimeUpdate() {
        apiService.getRealtimeUpdate().enqueue(new Callback<List<RealtimeUpdate>>() {
            @Override
            public void onResponse(Call<List<RealtimeUpdate>> call, Response<List<RealtimeUpdate>> response) {
                List<RealtimeUpdate> updateList = response.body();
                if (updateList != null) {
                    realtimeUpdateList.setValue(updateList);
                }
            }

            @Override
            public void onFailure(Call<List<RealtimeUpdate>> call, Throwable t) {
                Log.d(MapsViewModel.class.getSimpleName(), "Request to get realtime update from server failed");
            }
        });
    }

    public MutableLiveData<Route> getRouteByRouteId(String routeId) {
        final MutableLiveData<Route> route = new MutableLiveData<>();
        apiService.getRouteByRouteId(routeId).enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                if (response.body() != null) {
                    Route route1 = response.body().get(0);
                    Log.e(TAG, "onSuccess: getRouteByRouteId " + route1.getLongName());
                    route.setValue(route1);
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                Log.e(TAG, "onFailure: getRouteByRouteId " + t.getMessage());
            }
        });
        return route;
    }

    public void scheduleRealtimeUpdates() {
        int intervalInMillis = 2500;
        realtimeUpdateHandler.postDelayed(new Runnable() {
            public void run() {
                fetchRealtimeUpdate();
                realtimeUpdateHandler.postDelayed(this, intervalInMillis);
            }
        }, intervalInMillis);
    }

}
