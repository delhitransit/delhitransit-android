package com.delhitransit.delhitransit_android.fragment.stop_details;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;

import java.util.List;

public class StopDetailsViewModel extends ViewModel {
    private final MutableLiveData<List<RoutesFromStopDetail>> allRoutes = new MutableLiveData<>();

    private void makeApiRequest(int stopId) {
        //TODO make a call to the API and update the LiveData
    }

    public MutableLiveData<List<RoutesFromStopDetail>> getAllRoutes(int stopId) {
        makeApiRequest(stopId);
        return allRoutes;
    }

}