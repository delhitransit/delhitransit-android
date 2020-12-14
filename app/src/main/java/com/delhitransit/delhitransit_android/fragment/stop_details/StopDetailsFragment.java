package com.delhitransit.delhitransit_android.fragment.stop_details;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delhitransit.delhitransit_android.R;

public class StopDetailsFragment extends Fragment {

    private StopDetailsViewModel mViewModel;

    public static StopDetailsFragment newInstance() {
        return new StopDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stop_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StopDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

}