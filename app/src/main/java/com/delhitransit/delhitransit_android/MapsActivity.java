package com.delhitransit.delhitransit_android;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.BusStopsSuggestion;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.DataClass;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskCompleteCallback {

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private CardView searchCardView;
    private SearchView busStopSearchView_1, busStopSearchView_2;
    private int statusBarHeight;
    private boolean isKeyboardOn = false;
    private LinearLayout tryLayout;
    private SimpleCursorAdapter searchViewAdapter_1;
    private ApiInterface apiService;
    private ArrayList<StopsResponseData> listOfCityName = new ArrayList<>();
    private FloatingSearchView floatingBusStopSearchView_1;
    private SpinKitView progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        apiService = ApiClient.getApiService();
        setStatusBar();


        floatingBusStopSearchView_1 = findViewById(R.id.floating_bus_stop_search_view_1);
        progressBar = findViewById(R.id.progress_bar);


        floatingBusStopSearchView_1.setVisibility(View.GONE);
        floatingBusStopSearchView_1.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    floatingBusStopSearchView_1.clearSuggestions();
                } else {
                    floatingBusStopSearchView_1.showProgress();
                    apiService.getStopsByName(newQuery, false).enqueue(new Callback<List<StopsResponseData>>() {
                        @Override
                        public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                            if (response.body() != null) {
                                List<BusStopsSuggestion> busStopsSuggestions = new ArrayList<>();
                                for (StopsResponseData stopsResponseData : response.body()) {
                                    busStopsSuggestions.add(new BusStopsSuggestion(stopsResponseData));
                                }
                                floatingBusStopSearchView_1.swapSuggestions(busStopsSuggestions);
                            }
                            floatingBusStopSearchView_1.hideProgress();
                        }

                        @Override
                        public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                            Log.e(TAG, "onFailure: int " + t.getMessage());
                            floatingBusStopSearchView_1.hideProgress();
                        }
                    });
                }
            }
        });
        floatingBusStopSearchView_1.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                BusStopsSuggestion suggestion = (BusStopsSuggestion) searchSuggestion;
                setBusStopMarker(suggestion.getStopsResponseData());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                apiService.getStopsByName(currentQuery, true).enqueue(new Callback<List<StopsResponseData>>() {
                    @Override
                    public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                        if (response.body() != null) {
                            if (response.body().size() != 0) {
                                setBusStopMarker(response.body().get(0));
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

        /*busStopSearchView_1 = findViewById(R.id.bus_stop_search_view_1);
        busStopSearchView_2 = findViewById(R.id.bus_stop_search_view_2);
        searchCardView = findViewById(R.id.search_card_view_1);
        tryLayout = findViewById(R.id.try_layout_for_start);

        busStopSearchView_1.setIconified(false);
        busStopSearchView_1.clearFocus();*/
       /* busStopSearchView_1.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MapsActivity.this, "onClose", Toast.LENGTH_SHORT).show();
                shiftDown(searchCardView);
                return true;
            }
        });*/
        /*busStopSearchView_2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });*/


       /* Call<List<StopsResponseData>> getStopsCall = apiService.getStops();
        getStopsCall.enqueue(new Callback<List<StopsResponseData>>() {
            @Override
            public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                if (response.body() != null) {
                    listOfCityName.clear();
                    listOfCityName.addAll(response.body());

                    MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
                    int i = 0;
                    for (StopsResponseData s : listOfCityName) {
                        c.addRow(new Object[]{i++, s.getName()});
                    }
                    searchViewAdapter_1.changeCursor(c);

                    Log.e(TAG, "onResponse: done");
                }
            }

            @Override
            public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                Log.e(TAG, "onFailure: int " + t.getMessage());
            }
        });*/




       /* final String[] from = new String[]{"cityName"};
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
                }*//*
                for (int j = 0; j < 20; j++) {
                    c.addRow(new Object[]{j, "NONE"});
                }*//*

                searchViewAdapter_1.changeCursor(c);
                return true;
            }
        });*/


        //place1 = new MarkerOptions().position(new LatLng(22.3039, 70.8022)).title("Location 1");
        // place2 = new MarkerOptions().position(new LatLng(23.0225, 72.5714)).title("Location 2");
    }

    private void setBusStopMarker(StopsResponseData stopsResponseData) {
        LatLng latLng = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stops_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        floatingBusStopSearchView_1.clearSearchFocus();
    }

    private void setStatusBar() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.status_bar_bg).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


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
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardOn) {
            shiftDown(searchCardView);
            isKeyboardOn = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        progressBar.setVisibility(View.GONE);
        floatingBusStopSearchView_1.setVisibility(View.VISIBLE);
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
}