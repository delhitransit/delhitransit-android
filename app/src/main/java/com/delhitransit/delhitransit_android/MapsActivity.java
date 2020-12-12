package com.delhitransit.delhitransit_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.adapter.RoutesListAdapter;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.BusStopsSuggestion;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.helperclasses.ViewMarker;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.route.CustomizeRouteDetail;
import com.delhitransit.delhitransit_android.pojos.route.RouteDetailForAdapter;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskCompleteCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private final int LOCATION_ON_REQUEST_CODE = 101;

    private GoogleMap mMap;
    private Polyline currentPolyline;
    private ApiInterface apiService;
    private FloatingSearchView searchView1, searchView2;
    private SpinKitView progressBar;
    private Button bottomButton;
    private BottomSheetDialog routesBottomSheetDialog;
    private RecyclerView routesListRecycleView;
    private RoutesListAdapter routesListAdapter;
    private List<RouteDetailForAdapter> routesList = new ArrayList<>();
    private String currQuery = "";
    private Integer sourceId, destinationId;
    private LatLng source, destination, userLocation;
    private String sourceBusStopName, destinationBusStopName;
    private HashMap<Marker, StopsResponseData> nearByBusStopsHashMap = new HashMap<>();
    private TextView noRoutesAvailableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        apiService = ApiClient.getApiService(this);

        setMapFragment();
        setStatusBar();
        init();

    }

    private void setMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setStatusBar() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.status_bar_bg).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void init() {
        searchView1 = findViewById(R.id.floating_bus_stop_search_view_1);
        searchView2 = findViewById(R.id.floating_bus_stop_search_view_2);
        bottomButton = findViewById(R.id.bottom_button);
        progressBar = findViewById(R.id.progress_bar);

        viewVisibility(searchView1, false);
        viewVisibility(searchView2, false);
        viewVisibility(bottomButton, false);

        setSearchViewQueryAndSearchListener(searchView1, false);
        setSearchViewQueryAndSearchListener(searchView2, true);
        setRoutesBottomSheetDialog();
    }

    private void setRoutesBottomSheetDialog() {
        routesBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        routesBottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.routes_bottom_sheet_view, null));

        routesListRecycleView = routesBottomSheetDialog.findViewById(R.id.routes_list_recycle_view);
        noRoutesAvailableTextView = routesBottomSheetDialog.findViewById(R.id.no_routes_available_text_view);
        routesListAdapter = new RoutesListAdapter(this, routesList, () -> {
            progressBarVisibility(true);
            routesBottomSheetDialog.dismiss();
        });
        routesListRecycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        routesListRecycleView.setAdapter(routesListAdapter);

    }

    private void viewVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void progressBarVisibility(boolean visible) {
        viewVisibility(progressBar, visible);
    }

    private void setSearchViewQueryAndSearchListener(FloatingSearchView searchView, boolean isSecondSearchView) {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!isSecondSearchView) {
                    viewVisibility(searchView2, false);
                }
                if (newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else if (!newQuery.trim().equals("")) {
                    currQuery = newQuery;
                    searchView.showProgress();
                    apiService.getStopsByName(newQuery, false).enqueue(new Callback<List<StopsResponseData>>() {
                        @Override
                        public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                            if (response.body() != null) {
                                List<BusStopsSuggestion> busStopsSuggestions = new ArrayList<>();
                                for (StopsResponseData stopsResponseData : response.body()) {
                                    busStopsSuggestions.add(new BusStopsSuggestion(stopsResponseData));
                                }
                                searchView.swapSuggestions(busStopsSuggestions);
                            }
                            searchView.hideProgress();
                        }

                        @Override
                        public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                            Log.e(TAG, "onFailure: int " + t.getMessage());
                            searchView.hideProgress();
                        }
                    });

                }
            }
        });
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                StopsResponseData stopsDetail = ((BusStopsSuggestion) searchSuggestion).getStopsResponseData();
                setStopDataOnSearchView(stopsDetail, searchView, isSecondSearchView);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                apiService.getStopsByName(currentQuery, true).enqueue(new Callback<List<StopsResponseData>>() {
                    @Override
                    public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                        if (response.body() != null) {
                            if (response.body().size() != 0) {
                                setStopDataOnSearchView(response.body().get(0), searchView, isSecondSearchView);
                            } else {
                                showToast("Sorry ,No bus stop with \"" + currentQuery + "\" found");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                        Log.e(TAG, "onFailure: int " + t.getMessage());
                    }
                });

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
            textView.setTextColor(getColor(R.color.black));
            textView.setText(content);
        });
    }

    private void setStopDataOnSearchView(StopsResponseData stopsDetail, FloatingSearchView searchView, boolean isSecondSearchView) {
        if (isSecondSearchView) {
            destinationId = stopsDetail.getStopId();
            destinationBusStopName = stopsDetail.getName();

            progressBarVisibility(true);

            apiService.getCustomizeRoutesBetweenStops(destinationId, sourceId, ((int) TimeConverter.getSecondsSince12AM())).enqueue(new Callback<List<CustomizeRouteDetail>>() {
                @Override
                public void onResponse(Call<List<CustomizeRouteDetail>> call, Response<List<CustomizeRouteDetail>> response) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        routesList.clear();
                        routesListAdapter.setSourceAndDestination(source, destination);
                        routesListAdapter.setSourceBusStopName(sourceBusStopName);
                        routesList.addAll(makeListAdapter(response.body()));
                        routesListAdapter.notifyDataSetChanged();

                        viewVisibility(noRoutesAvailableTextView, false);
                        viewVisibility(routesListRecycleView, true);
                    } else {
                        viewVisibility(routesListRecycleView, false);
                        viewVisibility(noRoutesAvailableTextView, true);
                    }

                    routesBottomSheetDialog.show();
                    progressBarVisibility(false);
                    viewVisibility(bottomButton, true);
                }

                @Override
                public void onFailure(Call<List<CustomizeRouteDetail>> call, Throwable t) {

                }
            });
        } else {
            sourceId = stopsDetail.getStopId();
            sourceBusStopName = stopsDetail.getName();
            viewVisibility(searchView2, true);
            searchView2.setSearchFocused(true);
        }
        setBusStopMarker(stopsDetail, isSecondSearchView);
        searchView.setSearchText(stopsDetail.getName());
    }

    private List<RouteDetailForAdapter> makeListAdapter(List<CustomizeRouteDetail> customizeRouteDetailList) {
        List<RouteDetailForAdapter> list = new ArrayList<>();
        for (CustomizeRouteDetail customizeRouteDetail : customizeRouteDetailList) {
            for (String busTiming : customizeRouteDetail.getBusTimings()) {
                list.add(new RouteDetailForAdapter(customizeRouteDetail.getTravelTime(),
                        customizeRouteDetail.getRouteId(),
                        customizeRouteDetail.getTripId(),
                        TimeConverter.getTimeInSeconds(busTiming),
                        customizeRouteDetail.getLongName()));
            }
        }
        list.sort(Comparator.comparingLong(RouteDetailForAdapter::getBusTimings));
        return list;
    }

    private void setBusStopMarker(StopsResponseData stopsResponseData, boolean isSecondSearchView) {
        mMap.clear();
        if (!isSecondSearchView) {
            source = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 17));
            searchView1.clearSearchFocus();
        } else {
            destination = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 17));
            searchView2.clearSearchFocus();
        }
        addMarkerIfNotNull(source, "From : " + sourceBusStopName);
        addMarkerIfNotNull(destination, "To : " + destinationBusStopName);
        addMarkerIfNotNull(userLocation, "Your Location");
    }

    private void addMarkerIfNotNull(LatLng latLng, String s) {
        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(this, s).getBitmap())).position(latLng));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_ON_REQUEST_CODE) {
            getUserLocation();
        }
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        progressBarVisibility(false);
        viewVisibility(searchView1, true);

        LatLng latLng = new LatLng(28.6172368, 77.2059964);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        getUserLocation();
        mMap.setPadding(100, 600, 100, 100);
        mMap.setOnMarkerClickListener(marker -> {
            if (nearByBusStopsHashMap.containsKey(marker)) {
                setStopDataOnSearchView(nearByBusStopsHashMap.get(marker), searchView1, false);
            }
            return true;
        });

    }

    private void getUserLocation() {
        if (checkPermissions()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (isLocationEnabled(locationManager)) {
                try {
                    progressBarVisibility(true);
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            progressBarVisibility(false);
                            setUserLocation();
                            setNearByBusStopsWithInDistance(userLocation.latitude, userLocation.longitude, 1);
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
                }
            } else {
                Snackbar.make(findViewById(R.id.map), "Please turn on your location", Snackbar.LENGTH_LONG)
                        .setAction("TURN ON", v -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, LOCATION_ON_REQUEST_CODE);
                        })
                        .show();
            }

        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void setNearByBusStopsWithInDistance(double userLatitude, double userLongitude, double dist) {
        if (dist < 5) {
            apiService.getNearByStops(dist, userLatitude, userLongitude)
                    .enqueue(new Callback<List<StopsResponseData>>() {
                        @Override
                        public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                            if (response.body() != null) {
                                if (response.body().size() > 4) {
                                    nearByBusStopsHashMap = new HashMap<>();
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(new LatLng(userLatitude, userLongitude));
                                    for (StopsResponseData data : response.body()) {
                                        LatLng latLng = new LatLng(data.getLatitude(), data.getLongitude());
                                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(MapsActivity.this, data.getName(), Color.RED).getBitmap())));
                                        nearByBusStopsHashMap.put(marker, data);
                                        builder.include(latLng);
                                    }
                                    LatLngBounds bounds = builder.build();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                                } else {
                                    setNearByBusStopsWithInDistance(userLatitude, userLongitude, (dist + 0.25));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
        }
    }

    private void setUserLocation() {
        if (userLocation != null) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(this, "Your location ").getBitmap())).position(userLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
        }
    }

    private boolean isLocationEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void showRoutesBottomSheet(View view) {
        routesBottomSheetDialog.show();
    }


    @Override
    public void onTaskDone(Object... values) {
        if (!(values[0] instanceof Boolean)) {
            if (currentPolyline != null)
                currentPolyline.remove();
            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(this).getBitmap())).anchor(0.5f, 0.5f).position(currentPolyline.getPoints().get(0)));
            marker.setZIndex(2);
            marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(this).getBitmap())).anchor(0.5f, 0.5f).position(currentPolyline.getPoints().get(currentPolyline.getPoints().size() - 1)));
            marker.setZIndex(2);
        } else {
            routesBottomSheetDialog.dismiss();
            showToast("Route plotting not available for this trip");
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(source).include(destination).build(), 0));
        progressBarVisibility(false);
    }


    private void showToast(String s) {
        showToast(s, "info");
    }

    private void showToast(String s, String about) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        Log.e(TAG, about + "  : " + s);
    }




}
