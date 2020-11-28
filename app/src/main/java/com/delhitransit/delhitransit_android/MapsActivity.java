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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.adapter.RoutesListAdapter;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.BusStopsSuggestion;
import com.delhitransit.delhitransit_android.helperclasses.ViewMarker;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.Route;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
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

    FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private CardView searchCardView;
    private SearchView busStopSearchView_1, busStopSearchView_2;
    private int statusBarHeight;
    private boolean isKeyboardOn = false;
    private LinearLayout tryLayout;
    private SimpleCursorAdapter searchViewAdapter_1;
    private ApiInterface apiService;
    private ArrayList<StopsResponseData> listOfCityName = new ArrayList<>();
    private FloatingSearchView floatingBusStopSearchView_1, floatingBusStopSearchView_2;
    private SpinKitView progressBar;
    //private double userLatitude, userLongitude;
    private Button bottomButton;
    private BottomSheetDialog routesBottomSheetDialog;
    private RecyclerView routesListRecycleView;
    private RoutesListAdapter routesListAdapter;
    private List<Route> routesList = new ArrayList<>();
    private String currQuery = "";
    private Integer sourceId, destinationId;
    private LatLng source, destination, userLocation;
    private String sourceBusStopName, destinationBusStopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        apiService = ApiClient.getApiService();

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
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.status_bar_bg).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void init() {
        floatingBusStopSearchView_1 = findViewById(R.id.floating_bus_stop_search_view_1);
        floatingBusStopSearchView_2 = findViewById(R.id.floating_bus_stop_search_view_2);
        bottomButton = findViewById(R.id.bottom_button);
        progressBar = findViewById(R.id.progress_bar);


        viewVisibility(floatingBusStopSearchView_1, false);
        viewVisibility(floatingBusStopSearchView_2, false);
        viewVisibility(bottomButton, false);


        setSearchViewQueryAndSearchListener(floatingBusStopSearchView_1, false);
        setSearchViewQueryAndSearchListener(floatingBusStopSearchView_2, true);
        setRoutesBottomSheetDialog();
    }

    private void setRoutesBottomSheetDialog() {
        routesBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        routesBottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.routes_bottom_sheet_view, null));

        routesListRecycleView = routesBottomSheetDialog.findViewById(R.id.routes_list_recycle_view);
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
                    viewVisibility(floatingBusStopSearchView_2, false);
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
                BusStopsSuggestion suggestion = (BusStopsSuggestion) searchSuggestion;
                if (isSecondSearchView) {
                    destinationId = suggestion.getStopsResponseData().getStopId();
                    destinationBusStopName = suggestion.getStopsResponseData().getName();
                    apiService.getRoutesBetweenStops(destinationId, sourceId).enqueue(new Callback<List<Route>>() {
                        @Override
                        public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                            if (response.body() != null && !response.body().isEmpty()) {
                                routesList.clear();
                                routesListAdapter.setSourceAndDestination(source, destination);
                                routesListAdapter.setSourceBusStopName(sourceBusStopName);
                                routesList.addAll(response.body());
                                routesListAdapter.notifyDataSetChanged();
                                viewVisibility(routesBottomSheetDialog.findViewById(R.id.no_routes_available_text_view), false);
                                viewVisibility(routesListRecycleView, true);
                            } else {
                                viewVisibility(routesListRecycleView, false);
                                viewVisibility(routesBottomSheetDialog.findViewById(R.id.no_routes_available_text_view), true);
                            }

                            routesBottomSheetDialog.show();
                            viewVisibility(bottomButton, true);
                        }

                        @Override
                        public void onFailure(Call<List<Route>> call, Throwable t) {

                        }
                    });
                } else {
                    sourceId = suggestion.getStopsResponseData().getStopId();
                    sourceBusStopName = suggestion.getStopsResponseData().getName();
                    viewVisibility(floatingBusStopSearchView_2, true);
                    floatingBusStopSearchView_2.setSearchFocused(true);
                }
                setBusStopMarker(suggestion.getStopsResponseData(), isSecondSearchView);
                searchView.setSearchText(suggestion.getStopsResponseData().getName());
            }

            @Override
            public void onSearchAction(String currentQuery) {
               /* apiService.getStopsByName(currentQuery, true).enqueue(new Callback<List<StopsResponseData>>() {
                    @Override
                    public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                        if (response.body() != null) {
                            if (response.body().size() != 0) {
                                setBusStopMarker(response.body().get(0));
                                viewVisibility(selectDestinationButton, true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                        Log.e(TAG, "onFailure: int " + t.getMessage());
                    }
                });*/

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

    private void setBusStopMarker(StopsResponseData stopsResponseData, boolean isSecondSearchView) {
        mMap.clear();
        if (!isSecondSearchView) {
            source = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 17));
            floatingBusStopSearchView_1.clearSearchFocus();
        } else {
            destination = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 17));
            floatingBusStopSearchView_2.clearSearchFocus();
        }
        addMarkerIfNotNull(source, "From : " + sourceBusStopName);
        addMarkerIfNotNull(destination, "To : " + destinationBusStopName);
        addMarkerIfNotNull(userLocation, "Your Location");

        /*LatLng latLng = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        setUserLocation(false);
        floatingBusStopSearchView_1.clearSearchFocus();*/
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
        viewVisibility(floatingBusStopSearchView_1, true);

        LatLng latLng = new LatLng(28.6172368, 77.2059964);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        getUserLocation();

    }

    private void getUserLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkPermissions()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (isLocationEnabled(locationManager)) {
                try {
                    progressBarVisibility(true);
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
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
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(new LatLng(userLatitude, userLongitude));
                                    for (StopsResponseData data : response.body()) {
                                        LatLng latLng = new LatLng(data.getLatitude(), data.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(new ViewMarker(MapsActivity.this, data.getName(), Color.RED).getBitmap())));
                                        builder.include(latLng);
                                    }
                                    LatLngBounds bounds = builder.build();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
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
    public void onBackPressed() {
        if (isKeyboardOn) {
            //shiftDown(searchCardView);
            isKeyboardOn = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (!(values[0] instanceof Boolean)) {
            if (currentPolyline != null)
                currentPolyline.remove();
            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        } else {
            routesBottomSheetDialog.dismiss();
            showToast("Route plotting not available for this trip");
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(source).include(destination).build(), 200));
        progressBarVisibility(false);
    }


    private void showToast(String s) {
        showToast(s, "info");
    }

    private void showToast(String s, String about) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        Log.e(TAG, about + "  : " + s);
    }

    /*private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }*/
    /*busStopSearchView_1 = findViewById(R.id.bus_stop_search_view_1);
        busStopSearchView_2 = findViewById(R.id.bus_stop_search_view_2);
        searchCardView = findViewById(R.id.search_card_view_1);
        tryLayout = findViewById(R.id.try_layout_for_start);

        busStopSearchView_1.setIconified(false);
        busStopSearchView_1.clearFocus();
        busStopSearchView_1.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MapsActivity.this, "onClose", Toast.LENGTH_SHORT).show();
                shiftDown(searchCardView);
                return true;
            }
        });
        busStopSearchView_2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                shiftDown(searchCardView);
                isKeyboardOn = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isKeyboardOn) {
                    shiftUp(searchCardView);
                }
                return false;
            }
        });

       final String[] from = new String[]{"cityName"};
        final int[] to = new int[]{android.R.id.text1};
        searchViewAdapter_1 = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        busStopSearchView_1.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchViewAdapter_1.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("cityName"));
                busStopSearchView_1.setQuery(txt, true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });
        busStopSearchView_1.setSuggestionsAdapter(searchViewAdapter_1);

        SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) busStopSearchView_1.findViewById(androidx.appcompat.R.id.search_src_text);

        mSearchSrcTextView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSearchSrcTextView.setDropDownAnchor((busStopSearchView_1).getId());

        busStopSearchView_1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                shiftDown(searchCardView);
                isKeyboardOn = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!isKeyboardOn) {
                    shiftUp(searchCardView);
                    isKeyboardOn = true;
                }

                MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
                int i = 0;
                if (!listOfCityName.isEmpty()) {
                    for (StopsResponseData s : listOfCityName) {
                        if (s.getName().toLowerCase().startsWith(newText.toLowerCase())) {
                            c.addRow(new Object[]{i++, s.getName()});
                        }
                    }
                    for (int j = 0; j < 10; j++) {
                        c.addRow(new Object[]{j, "NONE"});
                    }

                } else {
                    c.addRow(new Object[]{i, "NONE"});
                }
                for (int j = 0; j < 20; j++) {
                    c.addRow(new Object[]{j, "NONE"});
                }

                searchViewAdapter_1.changeCursor(c);
                return true;
            }
        });*/


}