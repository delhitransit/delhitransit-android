package com.delhitransit.delhitransit_android.fragment.stop_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

public class StopDetailsFragment extends Fragment {

    private final StopDetail stop;
    private StopDetailsViewModel mViewModel;
    private RecyclerView recyclerView;
    private StopDetailsAdapter adapter;

    public StopDetailsFragment(StopDetail stop) {
        this.stop = stop;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.stop_details_fragment, container, false);
        recyclerView = parent.findViewById(R.id.stop_details_fragment_recycler_view);
        adapter = new StopDetailsAdapter(new StopDetailsAdapter.SDDiff());
        recyclerView.setAdapter(adapter);
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

}