package com.delhitransit.delhitransit_android.fragment.maps;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.pojos.RealtimeUpdate;
import com.delhitransit.delhitransit_android.pojos.route.CustomizeRouteDetail;
import com.delhitransit.delhitransit_android.pojos.route.Route;
import com.delhitransit.delhitransit_android.pojos.route.RouteDetailForAdapter;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsViewModel extends AndroidViewModel {

    public static final double NEARBY_STOPS_DEFAULT_DISTANCE = 1;
    private static final String TAG = MapsViewModel.class.getSimpleName();
    private final ApiInterface apiService = ApiClient.getApiService(getApplication().getApplicationContext());
    private final MutableLiveData<List<StopDetail>> nearbyStops = new MutableLiveData<>();
    private final MutableLiveData<List<RouteDetailForAdapter>> routesList = new MutableLiveData<>();
    private final MutableLiveData<List<RealtimeUpdate>> realtimeUpdateList = new MutableLiveData<>();
    private final HashMap<Long, List<StopDetail>> reachableStops = new HashMap<>();
    private final HashMap<Long, Long> reachableStopsRecentlyQuery = new HashMap<>();
    private double userLatitude;
    private double userLongitude;
    private StopDetail sourceStop;
    private StopDetail destinationStop;

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

    public void setRoutesList(List<CustomizeRouteDetail> customizeRouteDetailList) {
        List<RouteDetailForAdapter> list = new LinkedList<>();
        for (CustomizeRouteDetail customizeRouteDetail : customizeRouteDetailList) {
            for (String busTiming : customizeRouteDetail.getBusTimings()) {
                list.add(new RouteDetailForAdapter(
                        customizeRouteDetail.getTravelTime(),
                        customizeRouteDetail.getRouteId(),
                        customizeRouteDetail.getTripId(),
                        TimeConverter.getTimeInSeconds(busTiming),
                        customizeRouteDetail.getLongName()));
            }
        }
        list.sort(Comparator.comparingLong(RouteDetailForAdapter::getBusTimings));
        routesList.setValue(list);
    }

    public StopDetail getSourceStop() {
        return sourceStop;
    }

    public void setSourceStop(StopDetail sourceStop) {
        this.sourceStop = sourceStop;
        Application application = getApplication();
        if (application instanceof DelhiTransitApplication && ((DelhiTransitApplication) application).isDestinationStopsFiltered()) {
            getStopsReachableFromSourceStop();
        }
    }

    public StopDetail getDestinationStop() {
        return destinationStop;
    }

    public void setDestinationStop(StopDetail destinationStop) {
        this.destinationStop = destinationStop;
    }

    public MutableLiveData<List<StopDetail>> getStopsReachableFromSourceStop() {
        return getStopsReachableFrom(this.getSourceStop().getStopId());
    }

    public MutableLiveData<List<StopDetail>> getStopsReachableFrom(long stopId) {
        final MutableLiveData<List<StopDetail>> stops = new MutableLiveData<>();
        if (reachableStops.containsKey(stopId)) {
            stops.setValue(reachableStops.get(stopId));
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - reachableStopsRecentlyQuery.getOrDefault(stopId, 0L) < 4000) {
                return stops;
            } else reachableStopsRecentlyQuery.put(stopId, currentTime);
            apiService.getStopsReachableFromStop(stopId).enqueue(new Callback<List<StopDetail>>() {
                @Override
                public void onResponse(Call<List<StopDetail>> call, Response<List<StopDetail>> response) {
                    List<StopDetail> resp = response.body();
                    if (resp != null && resp.size() != 0) {
                        stops.setValue(resp);
                        reachableStops.put(stopId, resp);
                    }
                }

                @Override
                public void onFailure(Call<List<StopDetail>> call, Throwable t) {
                    Log.e(TAG, "onFailure: int " + t.getMessage());
                }
            });
        }
        return (stops);
    }

    public MutableLiveData<List<RealtimeUpdate>> getRealtimeUpdate() {
        apiService.getRealtimeUpdate().enqueue(new Callback<List<RealtimeUpdate>>() {
            @Override
            public void onResponse(Call<List<RealtimeUpdate>> call, Response<List<RealtimeUpdate>> response) {
                if (response.body() != null)
                    realtimeUpdateList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<RealtimeUpdate>> call, Throwable t) {
                Log.d(MapsViewModel.class.getSimpleName(), "Request to get realtime update from server failed");
            }
        });
        return realtimeUpdateList;
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
}
