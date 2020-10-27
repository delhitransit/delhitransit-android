package com.delhitransit.delhitransit_android;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.BusStopsSuggestion;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.DataClass;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
    private double userLatitude, userLongitude;
    private Button selectDestinationButton;
    private BottomSheetDialog routesBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getApiService();

        setMapFragment();
        setStatusBar();
        init();
        setContentView(R.layout.activity_maps);


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
        selectDestinationButton = findViewById(R.id.select_destination_button);
        progressBar = findViewById(R.id.progress_bar);


        viewVisibility(floatingBusStopSearchView_1, false);
        viewVisibility(floatingBusStopSearchView_2, false);
        viewVisibility(selectDestinationButton, false);


        setSearchViewQueryAndSearchListener(floatingBusStopSearchView_1, false);
        setSearchViewQueryAndSearchListener(floatingBusStopSearchView_2, true);
        setRoutesBottomSheetDialog();
    }


    private void setRoutesBottomSheetDialog() {
        routesBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        routesBottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.routes_bottom_sheet_view, null));
        routesBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        //routesBottomSheetDialog.pe
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
                if (newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else if (!newQuery.trim().equals("")) {
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
                setBusStopMarker(suggestion.getStopsResponseData());
                if (isSecondSearchView) {
                    routesBottomSheetDialog.show();
                } else {
                    viewVisibility(selectDestinationButton, true);
                }
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
    }

    private void setBusStopMarker(StopsResponseData stopsResponseData) {
        LatLng latLng = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        setUserLocation(false);
        floatingBusStopSearchView_1.clearSearchFocus();
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

        //new DataClass(MapsActivity.this).execute(DataClass.data2);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        /*mMap.addMarker(place1);
        mMap.addMarker(place2);
        new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(22.7739, 71.6673))
                .zoom(7)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);*/


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
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                            progressBarVisibility(false);
                            setUserLocation(true);
                            setNearByBusStopsWithInDistance(userLatitude, userLongitude, 1);
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
                Snackbar.make(findViewById(R.id.map), "Please turn on your location", Snackbar.LENGTH_INDEFINITE)
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
                                        mMap.addMarker(new MarkerOptions().position(latLng));
                                        builder.include(latLng);
                                    }
                                    LatLngBounds bounds = builder.build();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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

    private void setUserLocation(boolean isZoom) {
        LatLng latLng = new LatLng(userLatitude, userLongitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        if (isZoom) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private boolean isLocationEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /*
    public void shiftUp(View view) {
        //view.animate().setDuration(200).translationY((float) (1.8 * (view.getTop()) - statusBarHeight)).start();
        int cx = tryLayout.getMeasuredWidth() / 2;
        int cy = 0;

        if (busStopSearchView_1.isAttachedToWindow()) {
            int finalRadius = Math.max(tryLayout.getWidth(), tryLayout.getHeight()) / 2;
            Animator anim = ViewAnimationUtils.createCircularReveal(tryLayout, cx, cy, 0, finalRadius).setDuration(500);
            tryLayout.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    public void shiftDown(View view) {
        //view.animate().setDuration(200).translationY(0).start();
        int cx = tryLayout.getMeasuredWidth() / 2;
        int cy = tryLayout.getMeasuredWidth();

        int initialRadius = tryLayout.getWidth() / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(tryLayout, cx, cy, initialRadius, 0).setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tryLayout.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }*/

    public void showSecondSearchView(View view) {
        viewVisibility(floatingBusStopSearchView_2, true);
        viewVisibility(selectDestinationButton, false);
     /*   floatingBusStopSearchView_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });*/
        floatingBusStopSearchView_2.setSearchFocused(true);
        //floatingBusStopSearchView_2.requestFocusFromTouch();
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
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        mMap.addMarker(new MarkerOptions().position(new LatLng(((DataClass.Points) values[1]).lat, ((DataClass.Points) values[1]).lon)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(((DataClass.Points) values[2]).lat, ((DataClass.Points) values[2]).lon)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(((DataClass.Points) values[1]).lat, ((DataClass.Points) values[1]).lon), 15));
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