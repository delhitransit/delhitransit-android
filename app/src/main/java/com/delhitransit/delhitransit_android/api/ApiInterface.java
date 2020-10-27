package com.delhitransit.delhitransit_android.api;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("v1/stops/name/{query}")
    Call<List<StopsResponseData>> getStopsByName(@Path("query") String query, @Query("exact") boolean searchExact);


    @GET("v1/stops/nearby")
    Call<List<StopsResponseData>> getNearByStops(@Query("dist") double dist, @Query("lat") double lat, @Query("lon") double lon);

}
