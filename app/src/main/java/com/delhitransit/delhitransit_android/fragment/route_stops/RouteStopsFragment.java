package com.delhitransit.delhitransit_android.fragment.route_stops;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.adapter.RouteStopsAdapter;

public class RouteStopsFragment extends Fragment {

    private final String tripId;
    private RouteStopsViewModel mViewModel;
    private RouteStopsAdapter adapter;

    public RouteStopsFragment(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_route_details, container, false);
        //Setup the recycler view
        RecyclerView recyclerView = parent.findViewById(R.id.route_details_fragment_recycler_view);
        adapter = new RouteStopsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RouteStopsViewModel.class);
        if (tripId == null || tripId.isEmpty()) return;
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        mViewModel.getAllStops(tripId).observe(lifecycleOwner, adapter::submitList);
    }

}