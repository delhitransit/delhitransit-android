package com.delhitransit.delhitransit_android.fragment.route_stops;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.adapter.RouteStopsAdapter;
import com.delhitransit.delhitransit_android.interfaces.FragmentFinisherInterface;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.material.appbar.MaterialToolbar;

public class RouteStopsFragment extends Fragment {

    public static final String KEY_FRAGMENT_BACKSTACK = RouteStopsFragment.class.getSimpleName() + System.currentTimeMillis();
    private RoutesFromStopDetail route;
    private RouteStopsViewModel mViewModel;
    private RouteStopsAdapter adapter;
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_route_details, container, false);
        RouteStopsFragmentArgs args = RouteStopsFragmentArgs.fromBundle(getArguments());
        this.route = args.getRouteFromStopDetail();
        StopDetail stop = args.getStopDetail();
        activity = getActivity();
        //Setup the toolbar
        MaterialToolbar toolbar = parent.findViewById(R.id.route_details_fragment_app_bar);
        toolbar.setTitle(route.getRouteLongName() + " " + route.getLastStopName());
        if (stop != null)
            toolbar.setSubtitle("From " + stop.getName());
        toolbar.setNavigationOnClickListener(item -> finishMe(null));
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
        if (route == null) return;
        String tripId = route.getTripId();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        mViewModel.getAllStops(tripId).observe(lifecycleOwner, adapter::submitList);
    }

    public void finishMe(Runnable callback) {
        if (activity instanceof FragmentFinisherInterface) {
            ((FragmentFinisherInterface) activity).finishAndExecute(KEY_FRAGMENT_BACKSTACK, callback == null ? () -> {
            } : callback);
        }
    }

}