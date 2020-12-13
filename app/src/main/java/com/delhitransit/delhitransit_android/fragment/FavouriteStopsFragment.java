package com.delhitransit.delhitransit_android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;

public class FavouriteStopsFragment extends Fragment {

    private FavouriteStopsViewModel mViewModel;
    private FavouriteStopsAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.favourite_stops_fragment, container, false);
        RecyclerView recyclerView = parent.findViewById(R.id.fav_stops_recycler_view);
        adapter = new FavouriteStopsAdapter(new FavouriteStopsAdapter.FSDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = this.getContext();
        mViewModel = new ViewModelProvider(this).get(FavouriteStopsViewModel.class);
        mViewModel.setFavouriteStopsAdapter(adapter, this);
//        mViewModel.getAll().observe(getViewLifecycleOwner(), stops ->{
//            adapter.submitList(stops);
//        });
    }

}