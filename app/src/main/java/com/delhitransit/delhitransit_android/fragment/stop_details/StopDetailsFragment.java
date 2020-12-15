package com.delhitransit.delhitransit_android.fragment.stop_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.adapter.StopDetailsAdapter;
import com.delhitransit.delhitransit_android.interfaces.FragmentFinisherInterface;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.material.appbar.MaterialToolbar;

public class StopDetailsFragment extends Fragment {

    public static final String KEY_FRAGMENT_BACKSTACK = StopDetailsFragment.class.getSimpleName() + System.currentTimeMillis();
    private final StopDetail stop;
    private final Runnable fabClickCallback;
    private StopDetailsViewModel mViewModel;
    private RecyclerView recyclerView;
    private StopDetailsAdapter adapter;

    public StopDetailsFragment(StopDetail stop, Runnable fabClickCallback) {
        this.stop = stop;
        this.fabClickCallback = fabClickCallback;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.stop_details_fragment, container, false);
        //Setup the toolbar
        MaterialToolbar toolbar = parent.findViewById(R.id.stop_details_fragment_app_bar);
        toolbar.setTitle(stop.getName());
        toolbar.setNavigationOnClickListener(item -> finishMe(null));
        //Setup the recycler view
        recyclerView = parent.findViewById(R.id.stop_details_fragment_recycler_view);
        adapter = new StopDetailsAdapter();
        recyclerView.setAdapter(adapter);
        //Setup the navigate FAB
        Button fab = parent.findViewById(R.id.extended_fab);
        fab.setOnClickListener(item -> {
            finishMe(fabClickCallback);
        });
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StopDetailsViewModel.class);
        if (stop != null) {
            mViewModel.getAllRoutes(stop.getStopId()).observe(getViewLifecycleOwner(), adapter::submitList);
        }
    }

    private void finishMe(Runnable callback) {
        FragmentFinisherInterface activity = (FragmentFinisherInterface) getActivity();
        if (activity != null) {
            activity.finishAndExecute(KEY_FRAGMENT_BACKSTACK, callback == null ? () -> {
            } : callback);
        }
    }

}