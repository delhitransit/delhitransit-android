package com.delhitransit.delhitransit_android.api;

import com.delhitransit.delhitransit_android.pojos.ShapePoint;
import com.delhitransit.delhitransit_android.pojos.route.CustomizeRouteDetail;
import com.delhitransit.delhitransit_android.pojos.route.RouteDetail;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("v1/stops/name/{query}")
    Call<List<StopDetail>> getStopsByName(@Path("query") String query, @Query("exact") boolean searchExact);

    @GET("v1/stops/nearby")
    Call<List<StopDetail>> getNearByStops(@Query("dist") double dist, @Query("lat") double lat, @Query("lon") double lon);

    @GET("/v1/routes/between")
    Call<List<RouteDetail>> getRoutesBetweenStops(@Query("destination") Integer destination, @Query("source") Integer source);

    @GET("/v1/client/routes/between")
    Call<List<CustomizeRouteDetail>> getCustomizeRoutesBetweenStops(@Query("destination") Integer destination, @Query("source") Integer source, @Query("time") int time);

    @GET("/v1/shapePoints/trip/{trip}")
    Call<List<ShapePoint>> getAllShapePointsByTripId(@Path("trip") String query);

    @GET("/v1/client/routes/stop/{stop}")
    Call<List<RoutesFromStopDetail>> getAllRoutesByStopId(@Path("stop") int stopId, @Query("time") int time);

    @GET("/v1/stops/trip/{trip}")
    Call<List<StopDetail>> getStopsByTripId(@Path("trip") String tripId);
}
