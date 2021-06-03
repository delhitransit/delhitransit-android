package com.delhitransit.delhitransit_android.fragment.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.adapter.RoutesListAdapter;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.fragment.favourite_stops.FavouriteStopsViewModel;
import com.delhitransit.delhitransit_android.helperclasses.BusStopsSuggestion;
import com.delhitransit.delhitransit_android.helperclasses.CircleMarker;
import com.delhitransit.delhitransit_android.helperclasses.GeoLocationHelper;
import com.delhitransit.delhitransit_android.helperclasses.MarkerDetails;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.helperclasses.ViewMarker;
import com.delhitransit.delhitransit_android.interfaces.OnStopMarkerClickedListener;
import com.delhitransit.delhitransit_android.pojos.RealtimeUpdate;
import com.delhitransit.delhitransit_android.pojos.route.CustomizeRouteDetail;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import jp.wasabeef.blurry.Blurry;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {

    private static final String TAG = MapsFragment.class.getSimpleName();
    private final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private final int LOCATION_ON_REQUEST_CODE = 101;
    private final List<StopDetail> favouriteStopsLists = new ArrayList<>();
    private GoogleMap mMap;
    private Polyline currentPolyline;
    private ApiInterface apiService;
    private FloatingSearchView searchView1, searchView2;
    private CardView progressCardView;
    private Button bottomButton;
    private BottomSheetDialog routesBottomSheetDialog;
    private RecyclerView routesListRecycleView;
    private RoutesListAdapter routesListAdapter;
    private String currQuery = "";
    private MarkerDetails sourceMarkerDetail, destinationMarkerDetail;
    private LatLng userLocation;
    private HashMap<Marker, StopDetail> busStopsHashMap = new HashMap<>();
    private HashMap<Marker, RealtimeUpdate> realtimeUpdateHashMap = new HashMap<>();
    private TextView noRoutesAvailableTextView;
    private View parentView;
    private ImageView blurView;
    private Context context;
    private MaterialProgressBar horizontalProgressBar;
    private CircleMarker circleMarker;
    private MapsViewModel mViewModel;
    private DelhiTransitApplication application;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            progressBarVisibility(false);
            viewVisibility(searchView1, true);
            LatLng latLng = new LatLng(28.6172368, 77.2059964);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            mMap.setPadding(100, 600, 100, 100);
            mMap.clear();
            if (mViewModel != null) {
                StopDetail sourceStop = mViewModel.getSourceStop();
                if (sourceStop != null) {
                    setStopDataOnSearchView(sourceStop, searchView1, false);
                }
            }
            setOnMarkerClickListeners();
            getUserLocation();

        }

    };
    private LifecycleOwner mLifecycleOwner;
    private FloatingActionButton mFlipSearchItemsFab;

    private void setOnMarkerClickListeners() {
        mMap.setOnMarkerClickListener(marker -> {
            StopDetail stop = busStopsHashMap.get(marker);
            RealtimeUpdate realtimeUpdate = realtimeUpdateHashMap.get(marker);
            if (stop != null) {
                Runnable runnable = () -> setStopDataOnSearchView(stop, searchView1, false);
                Activity activity = getActivity();
                if (activity instanceof OnStopMarkerClickedListener) {
                    ((OnStopMarkerClickedListener) activity).onStopMarkerClick(stop, runnable);
                } else runnable.run();
            }
            if (realtimeUpdate != null) {
                setDialogBox(realtimeUpdate);
            }
            return true;
        });
    }

    private void setDialogBox(RealtimeUpdate realtimeUpdate) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        View mView = getLayoutInflater().inflate(R.layout.realtime_dialog_layout, null);
        TextView busRoute, busLastUpdate, busSpeed;
        Button moreButton;
        busRoute = mView.findViewById(R.id.realtime_route_text);
        busLastUpdate = mView.findViewById(R.id.realtime_updated_text);
        busSpeed = mView.findViewById(R.id.realtime_speed_text);
        RoutesFromStopDetail routesFromStopDetail = new RoutesFromStopDetail();
        alert.setView(mView);
        alert.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NavController navController = NavHostFragment.findNavController(MapsFragment.this);
                MapsFragmentDirections.ActionMapsFragmentToRouteStopsFragment action = MapsFragmentDirections.actionMapsFragmentToRouteStopsFragment(routesFromStopDetail);
                navController.navigate(action);

            }
        });
        alert.setTitle(realtimeUpdate.getVehicleID());
        busSpeed.setText(String.valueOf(realtimeUpdate.getSpeed() * 3.6));
        busLastUpdate.setText(String.valueOf(realtimeUpdate.getTimestamp()));
        mViewModel.getRouteByRouteId(realtimeUpdate.getRouteID()).observe(mLifecycleOwner, route -> {
            if (route != null) {
                busRoute.setText(route.getLongName());
                routesFromStopDetail.setEarliestTime(-1);
                routesFromStopDetail.setRouteLongName(route.getLongName());
                routesFromStopDetail.setRouteId(route.getRouteId());
                routesFromStopDetail.setLastStopName(" ");
                routesFromStopDetail.setTripId(realtimeUpdate.getTripID());
                Log.e(TAG, "setDialogBox: " + realtimeUpdate.getTripID());
            }
        });
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_map, container, false);
        context = this.getContext();
        application = (DelhiTransitApplication) context.getApplicationContext();
        setMapFragment();
        init();
        getAllFavouriteStops();
        return parentView;
    }

    private void setMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void init() {
        searchView1 = parentView.findViewById(R.id.floating_bus_stop_search_view_1);
        searchView2 = parentView.findViewById(R.id.floating_bus_stop_search_view_2);
        bottomButton = parentView.findViewById(R.id.bottom_button);
        progressCardView = parentView.findViewById(R.id.progress_bar);
        blurView = parentView.findViewById(R.id.blur_view);
        horizontalProgressBar = parentView.findViewById(R.id.horizontal_loading_bar);
        mFlipSearchItemsFab = parentView.findViewById(R.id.flip_search_items_fab);

        viewVisibility(searchView1, false);
        viewVisibility(searchView2, false);
        viewVisibility(bottomButton, false);

        setSearchViewQueryAndSearchListener(searchView1, false);
        setSearchViewQueryAndSearchListener(searchView2, true);
        setRoutesBottomSheetDialog();

        bottomButton.setOnClickListener(it -> routesBottomSheetDialog.show());
        mFlipSearchItemsFab.setOnClickListener(this::setSearchViewWithFlip);
    }


    public void setSearchViewWithFlip(View v) {
        StopDetail sourceStop = mViewModel.getSourceStop();
        StopDetail destinationStop = mViewModel.getDestinationStop();
        if (sourceStop != null && destinationStop != null) {
            setStopDataOnSearchView(destinationStop, searchView1, false);
            setStopDataOnSearchView(sourceStop, searchView2, true);
        }
    }

    private void getAllFavouriteStops() {
        FavouriteStopsViewModel favouriteStopsViewModel = new ViewModelProvider(this).get(FavouriteStopsViewModel.class);
        favouriteStopsViewModel.getAll().observe(getViewLifecycleOwner(), list -> {
            favouriteStopsLists.clear();
            favouriteStopsLists.addAll(list);
        });
    }

    private void setRoutesBottomSheetDialog() {
        routesBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        routesBottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.routes_bottom_sheet_view, null));

        routesListRecycleView = routesBottomSheetDialog.findViewById(R.id.routes_list_recycle_view);
        noRoutesAvailableTextView = routesBottomSheetDialog.findViewById(R.id.no_routes_available_text_view);

        routesListAdapter = new RoutesListAdapter(context, this::onRouteSelected, this::onTaskDone);

        routesListRecycleView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        routesListRecycleView.setAdapter(routesListAdapter);

    }

    private void onRouteSelected() {
        progressBarVisibility(true);
        routesBottomSheetDialog.dismiss();
    }

    private void onTaskDone(Object[] values) {
        if (!(values[0] instanceof Boolean)) {
            if (currentPolyline != null) {
                currentPolyline.remove();
                circleMarker.remove();
            }
            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
            circleMarker = new CircleMarker(mMap, context, currentPolyline);
        } else {
            routesBottomSheetDialog.dismiss();
            showToast("Route plotting not available for this trip");
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(sourceMarkerDetail.latLng).include(destinationMarkerDetail.latLng).build(), 0));
        progressBarVisibility(false);
    }

    private void viewVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void progressBarVisibility(boolean visible) {
        if (visible) {
            horizontalProgressBar.setVisibility(View.GONE);
            mMap.snapshot(bitmap -> {
                blurView.setVisibility(View.VISIBLE);
                blurView.setImageBitmap(bitmap);

                Blurry.with(context)
                        .radius(15)
                        .sampling(2)
                        .onto(parentView.findViewById(R.id.main_layout));

                viewVisibility(progressCardView, true);
            });

        } else {
            blurView.setVisibility(View.GONE);
            Blurry.delete(parentView.findViewById(R.id.main_layout));
            viewVisibility(progressCardView, false);
        }
    }

    private void setSearchViewQueryAndSearchListener(FloatingSearchView searchView, boolean isSecondSearchView) {
        searchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            if (!isSecondSearchView) {
                viewVisibility(searchView2, false);
                viewVisibility(mFlipSearchItemsFab, false);
            }
            if (newQuery.equals("")) {
                searchView.clearSuggestions();
                viewVisibility(mFlipSearchItemsFab, false);
            } else {
                if (!newQuery.trim().equals("")) {
                    currQuery = newQuery;
                    searchView.showProgress();

                    if (isSecondSearchView && application.isDestinationStopsFiltered()) {
                        MutableLiveData<List<StopDetail>> reachable = mViewModel.getStopsReachableFromSourceStop();
                        reachable.observe(mLifecycleOwner, stops -> {
                            if (stops != null) {
                                stops = stops.stream().filter(it -> it.getName().toUpperCase().contains(newQuery.toUpperCase())).collect(Collectors.toList());
                                List<BusStopsSuggestion> busStopsSuggestions = new ArrayList<>();
                                for (StopDetail stopsResponseData : stops) {
                                    if (favouriteStopsLists.contains(stopsResponseData)) {
                                        busStopsSuggestions.add(0, new BusStopsSuggestion(stopsResponseData, true));
                                    } else {
                                        busStopsSuggestions.add(new BusStopsSuggestion(stopsResponseData));
                                    }
                                }
                                searchView.swapSuggestions(busStopsSuggestions);
                            }
                            searchView.hideProgress();
                        });
                    } else {
                        apiService.getStopsByName(newQuery, false).enqueue(new Callback<List<StopDetail>>() {
                            @Override
                            public void onResponse(Call<List<StopDetail>> call, Response<List<StopDetail>> response) {
                                if (response.body() != null) {
                                    List<BusStopsSuggestion> busStopsSuggestions = new ArrayList<>();
                                    for (StopDetail stopsResponseData : response.body()) {
                                        if (favouriteStopsLists.contains(stopsResponseData)) {
                                            busStopsSuggestions.add(0, new BusStopsSuggestion(stopsResponseData, true));
                                        } else {
                                            busStopsSuggestions.add(new BusStopsSuggestion(stopsResponseData));
                                        }
                                    }
                                    searchView.swapSuggestions(busStopsSuggestions);
                                }
                                searchView.hideProgress();
                            }

                            @Override
                            public void onFailure(Call<List<StopDetail>> call, Throwable t) {
                                Log.e(TAG, "onFailure: int " + t.getMessage());
                                searchView.hideProgress();
                            }
                        });
                    }
                }
            }
        });
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                StopDetail stopsDetail = ((BusStopsSuggestion) searchSuggestion).getStopDetail();
                setStopDataOnSearchView(stopsDetail, searchView, isSecondSearchView);
                if (isSecondSearchView) viewVisibility(mFlipSearchItemsFab, true);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                searchView.showProgress();

                if (isSecondSearchView && application.isDestinationStopsFiltered()) {
                    MutableLiveData<List<StopDetail>> reachable = mViewModel.getStopsReachableFromSourceStop();
                    reachable.observe(mLifecycleOwner, stops -> {
                        if (stops != null && stops.size() != 0) {
                            stops = stops.stream().filter(it -> it.getName().toUpperCase().contains(currentQuery.toUpperCase())).collect(Collectors.toList());
                            if (stops != null && stops.size() != 0)
                                setStopDataOnSearchView(stops.get(0), searchView, isSecondSearchView);
                        } else {
                            showToast("Sorry ,No bus stop with \"" + currentQuery + "\" found");
                        }
                        searchView.hideProgress();
                        viewVisibility(mFlipSearchItemsFab, true);
                    });
                } else {
                    apiService.getStopsByName(currentQuery, true).enqueue(new Callback<List<StopDetail>>() {
                        @Override
                        public void onResponse(Call<List<StopDetail>> call, Response<List<StopDetail>> response) {

                            if (response.body() != null && response.body().size() != 0) {
                                setStopDataOnSearchView(response.body().get(0), searchView, isSecondSearchView);
                            } else {
                                showToast("Sorry ,No bus stop with \"" + currentQuery + "\" found");
                            }
                            searchView.hideProgress();
                            viewVisibility(mFlipSearchItemsFab, true);
                        }

                        @Override
                        public void onFailure(Call<List<StopDetail>> call, Throwable t) {
                            Log.e(TAG, "onFailure: int " + t.getMessage());
                            searchView.hideProgress();
                            viewVisibility(mFlipSearchItemsFab, true);
                        }
                    });
                }
            }
        });
        searchView.setOnBindSuggestionCallback((View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) -> {
            String temp = item.getBody();
            SpannableStringBuilder content = new SpannableStringBuilder(temp);
            if (temp.toLowerCase().contains(currQuery.toLowerCase())) {
                int index = temp.toLowerCase().indexOf(currQuery.toLowerCase());
                content.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        index,
                        index + currQuery.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            textView.setTextColor(context.getColor(R.color.black));
            textView.setText(content);
            if (((BusStopsSuggestion) item).isFavourite()) {
                leftIcon.setVisibility(View.VISIBLE);
                leftIcon.setImageResource(R.drawable.ic_baseline_star_24);
                leftIcon.setColorFilter(context.getColor(R.color.black));
            } else {
                leftIcon.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setStopDataOnSearchView(StopDetail stopsDetail, FloatingSearchView searchView, boolean isSecondSearchView) {

        MarkerDetails markerDetails = new MarkerDetails(stopsDetail, isSecondSearchView);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerDetails.latLng, 17));
        addMarkerIfNotNull(markerDetails);

        if (isSecondSearchView) {
            mViewModel.setDestinationStop(stopsDetail);
            if (destinationMarkerDetail != null) {
                destinationMarkerDetail.remove();
            }
            destinationMarkerDetail = markerDetails;
            searchView2.clearSearchFocus();
            progressBarVisibility(true);

            apiService.getCustomizeRoutesBetweenStops(destinationMarkerDetail.id, sourceMarkerDetail.id, ((int) TimeConverter.getSecondsSince12AM())).enqueue(new Callback<List<CustomizeRouteDetail>>() {
                @Override
                public void onResponse(Call<List<CustomizeRouteDetail>> call, Response<List<CustomizeRouteDetail>> response) {
                    boolean responseExists = response.body() != null && !response.body().isEmpty();
                    if (responseExists) {
                        routesListAdapter.setDetail(sourceMarkerDetail.latLng, destinationMarkerDetail.latLng, sourceMarkerDetail.name);
                        mViewModel.setRoutesList(response.body());
                        routesListAdapter.notifyDataSetChanged();
                    }
                    viewVisibility(noRoutesAvailableTextView, !responseExists);
                    viewVisibility(routesListRecycleView, responseExists);

                    routesBottomSheetDialog.show();
                    progressBarVisibility(false);
                    viewVisibility(bottomButton, true);
                }

                @Override
                public void onFailure(Call<List<CustomizeRouteDetail>> call, Throwable t) {

                }
            });
        } else {
            mViewModel.setSourceStop(stopsDetail);
            if (sourceMarkerDetail != null) {
                sourceMarkerDetail.remove();
            }
            sourceMarkerDetail = markerDetails;

            searchView1.clearSearchFocus();
            viewVisibility(searchView2, true);
            searchView2.setSearchFocused(true);
        }
        searchView.setSearchText(stopsDetail.getName());
    }

    private void addMarkerIfNotNull(MarkerDetails markerDetail) {
        if (markerDetail.latLng != null) {
            busStopsHashMap.remove(markerDetail.marker);
            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(context, markerDetail.name, markerDetail.relation, getMarkerType(markerDetail.stopsResponseData)).getBitmap())).position(markerDetail.latLng));
            marker.setZIndex(3);
            markerDetail.marker = marker;
            busStopsHashMap.put(marker, markerDetail.stopsResponseData);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_ON_REQUEST_CODE) {
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getUserLocation() {
        if (checkPermissions()) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (isLocationEnabled(locationManager)) {
                try {
                    searchView1.setSearchHint("Loading Nearby Bus Stops...");
                    horizontalProgressBar.setVisibility(View.VISIBLE);
                    String locationProvider = application.getLocationProvider();
                    locationManager.requestSingleUpdate(locationProvider, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            horizontalProgressBar.setVisibility(View.GONE);
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            userLocation = new LatLng(latitude, longitude);
                            setUserLocation();
                            mViewModel.setUserCoordinates(latitude, longitude);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }

                    }, null);
                } catch (SecurityException e) {
                    Log.e(TAG, "getLastLocation: " + e.getMessage());
                    e.printStackTrace();
                    horizontalProgressBar.setVisibility(View.GONE);
                }
            } else {
                Snackbar.make(parentView.findViewById(R.id.map), "Please turn on your location", Snackbar.LENGTH_LONG)
                        .setAction("TURN ON", v -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, LOCATION_ON_REQUEST_CODE);
                        })
                        .show();
                horizontalProgressBar.setVisibility(View.GONE);
            }

        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        FragmentActivity activity = this.getActivity();
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private int getMarkerType(StopDetail data) {
        int type = ViewMarker.BUS_STOP;
        for (StopDetail detail : favouriteStopsLists) {
            if (detail.equals(data)) {
                type = ViewMarker.FAVOURITE;
                break;
            }
        }
        return type;
    }

    private void setUserLocation() {
        if (userLocation != null) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(context, "Your location ").getBitmap())).position(userLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
        }
    }

    private boolean isLocationEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showToast(String s) {
        showToast(s, "info");
    }

    private void showToast(String s, String about) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        Log.e(TAG, about + "  : " + s);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        MapsFragmentArgs args = MapsFragmentArgs.fromBundle(getArguments());
        StopDetail sourceStop = args.getSourceStop();
        if (sourceStop != null) {
            mViewModel.setSourceStop(sourceStop);
        }
        apiService = mViewModel.getApiService();
        mLifecycleOwner = getViewLifecycleOwner();
        mViewModel.getNearbyStops().observe(mLifecycleOwner, this::setNearByBusStopsWithInDistance);
        mViewModel.getRoutesList().observe(mLifecycleOwner, routesListAdapter::submitList);
        mViewModel.getRealtimeUpdate().observe(mLifecycleOwner, this::setNearbyBusesRealtime);
    }

    private void setNearByBusStopsWithInDistance(List<StopDetail> nearbyStops) {
        busStopsHashMap = new HashMap<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(mViewModel.getUserLatitude(), mViewModel.getUserLongitude()));
        for (StopDetail data : nearbyStops) {
            LatLng latLng = new LatLng(data.getLatitude(), data.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(context, data.getName(), Color.RED, getMarkerType(data)).getBitmap())));
            busStopsHashMap.put(marker, data);
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        searchView1.setSearchHint("Search Bus Stops");
    }

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color, float scale) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(
                (int) (scale * vectorDrawable.getIntrinsicWidth()),
                (int) (scale * vectorDrawable.getIntrinsicHeight()),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setNearbyBusesRealtime(List<RealtimeUpdate> realtimeUpdateList) {
        Log.v(MapsFragment.class.getSimpleName(), String.valueOf(realtimeUpdateList.size()));
        double userLatitude = mViewModel.getUserLatitude();
        double userLongitude = mViewModel.getUserLongitude();
        GeoLocationHelper userLocationHelper = GeoLocationHelper.fromDegrees(userLatitude, userLongitude);
        realtimeUpdateList.forEach(realtimeUpdate -> {
            double distanceFromUser = userLocationHelper.distanceTo(GeoLocationHelper.fromDegrees(realtimeUpdate.getLatitude(), realtimeUpdate.getLongitude()), null);
            if (distanceFromUser <= 20) {
                LatLng latLng = new LatLng(realtimeUpdate.getLatitude(), realtimeUpdate.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(vectorToBitmap(R.drawable.bus_icon, Color.parseColor("#A4C639"), 0.5f))
                        .title(realtimeUpdate.getVehicleID()));
                realtimeUpdateHashMap.put(marker, realtimeUpdate);
            }
        });
    }
}