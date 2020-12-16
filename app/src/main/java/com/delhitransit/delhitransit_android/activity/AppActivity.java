package com.delhitransit.delhitransit_android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.MapsFragment;
import com.delhitransit.delhitransit_android.fragment.SettingsFragment;
import com.delhitransit.delhitransit_android.fragment.favourite_stops.FavouriteStopsFragment;
import com.delhitransit.delhitransit_android.fragment.route_stops.RouteStopsFragment;
import com.delhitransit.delhitransit_android.fragment.stop_details.StopDetailsFragment;
import com.delhitransit.delhitransit_android.interfaces.FragmentFinisherInterface;
import com.delhitransit.delhitransit_android.interfaces.OnRouteDetailsSelectedListener;
import com.delhitransit.delhitransit_android.interfaces.OnStopMarkerClickedListener;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class AppActivity extends AppCompatActivity implements OnStopMarkerClickedListener, FragmentFinisherInterface, OnRouteDetailsSelectedListener {

    public static final short MAPS_FRAGMENT = 0;
    public static final short SETTINGS_FRAGMENT = 1;
    public static final short FAVOURITE_STOPS_FRAGMENT = 2;
    private static final short STOP_DETAILS_FRAGMENT = 3;
    private static final short ROUTE_STOPS_FRAGMENT = 4;
    private final HashMap<Short, Fragment> fragmentMap = new HashMap<>();
    private short currentFragment = MAPS_FRAGMENT;
    private FragmentManager manager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        manager = getSupportFragmentManager();
        bottomNav = findViewById(R.id.bottom_navigation);
        navigateTo(currentFragment);
        setBottomNavigationSelectedTab(currentFragment);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.map_tab_button) {
                navigateTo(MAPS_FRAGMENT);
            } else if (itemId == R.id.settings_tab_button) {
                navigateTo(SETTINGS_FRAGMENT);
            } else if (itemId == R.id.fav_stops_tab_button) {
                navigateTo(FAVOURITE_STOPS_FRAGMENT);
            }
            return true;
        });
    }

    private void setBottomNavigationSelectedTab(short selectedTab) {
        switch (selectedTab) {
            case MAPS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.map_tab_button);
                break;
            case SETTINGS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.settings_tab_button);
                break;
            case FAVOURITE_STOPS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.fav_stops_tab_button);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(() -> {
            if (!isNetworkConnected()) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
                        .setTitle("Device offline")
                        .setMessage("Please turn on your mobile data or connect to a WiFi network")
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Turn on WiFi", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))).show());
            } else if (!isServerReachable()) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
                        .setTitle("Server unreachable")
                        .setMessage("Cannot connect to remote server. You can change the server IP or try again in a little while")
                        .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Change IP address", (dialog, which) -> findViewById(R.id.settings_tab_button).performClick()).show());
            }
        }).start();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isServerReachable() {
        try {
            URL serverIP = new URL(((DelhiTransitApplication) getApplication()).getServerIP());
            Socket socket = new Socket();
            int port = serverIP.getPort();
            socket.connect(new InetSocketAddress(serverIP.getHost(), port == -1 ? 80 : port), 5000);
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void navigateTo(short fragmentId) {
        FragmentTransaction transaction = manager.beginTransaction();
        List<Fragment> managerFragments = manager.getFragments();
        hideCurrentFragment(transaction, managerFragments);
        switch (fragmentId) {
            default:
            case MAPS_FRAGMENT: {
                showOrAddFragmentTransaction(MAPS_FRAGMENT, new MapsFragment(), managerFragments, transaction);
                break;
            }
            case SETTINGS_FRAGMENT: {
                showOrAddFragmentTransaction(SETTINGS_FRAGMENT, new SettingsFragment(), managerFragments, transaction);
                break;
            }
            case FAVOURITE_STOPS_FRAGMENT: {
                showOrAddFragmentTransaction(FAVOURITE_STOPS_FRAGMENT, new FavouriteStopsFragment(), managerFragments, transaction);
                break;
            }
            case STOP_DETAILS_FRAGMENT: {
                if (!fragmentMap.containsKey(STOP_DETAILS_FRAGMENT)) {
                    transaction = null;
                } else {
                    Fragment fragment = showOrAddFragmentTransaction(STOP_DETAILS_FRAGMENT, null, managerFragments, transaction);
                    transaction.addToBackStack(fragment instanceof StopDetailsFragment ? StopDetailsFragment.KEY_FRAGMENT_BACKSTACK : null);
                }
                break;
            }
            case ROUTE_STOPS_FRAGMENT: {
                if (!fragmentMap.containsKey(ROUTE_STOPS_FRAGMENT)) {
                    transaction = null;
                } else {
                    Fragment fragment = showOrAddFragmentTransaction(ROUTE_STOPS_FRAGMENT, null, managerFragments, transaction);
                    transaction.addToBackStack(fragment instanceof StopDetailsFragment ? RouteStopsFragment.KEY_FRAGMENT_BACKSTACK : null);
                }
            }
        }
        if (transaction != null) {
            transaction.commit();
        }
    }

    private void hideCurrentFragment(FragmentTransaction transaction, List<Fragment> managerFragments) {
        short previousFragment = currentFragment;
        Fragment prevFragment = fragmentMap.get(previousFragment);
        if (prevFragment != null && managerFragments.contains(prevFragment)) {
            prevFragment.onPause();
            transaction.hide(prevFragment);
        }
    }

    private Fragment showOrAddFragmentTransaction(short fragmentId, Fragment newInstance, List<Fragment> managerFragments, FragmentTransaction transaction) {
        Fragment currentFragment = fragmentMap.getOrDefault(fragmentId, newInstance);
        fragmentMap.put(fragmentId, currentFragment);
        this.currentFragment = fragmentId;
        if (!managerFragments.contains(currentFragment)) {
            transaction.add(R.id.fragment_container, currentFragment);
        } else {
            transaction.show(currentFragment);
            currentFragment.onResume();
        }
        return currentFragment;
    }

    @Override
    public void onStopMarkerClick(StopDetail stop, Runnable fabClickCallback) {
        if (stop == null) return;
        StopDetailsFragment fragment = new StopDetailsFragment(stop, fabClickCallback);
        fragmentMap.put(STOP_DETAILS_FRAGMENT, fragment);
        navigateTo(STOP_DETAILS_FRAGMENT);
    }

    @Override
    public void finishAndExecute(String backStackKey, @NotNull Runnable runOnFinish) {
        manager.popBackStackImmediate(backStackKey, POP_BACK_STACK_INCLUSIVE);
        getVisibleFragment().ifPresent(it -> navigateTo(getFragmentIdFromHashMap(it)));
        runOnFinish.run();
    }

    @Override
    public void onBackPressed() {
        Optional<Fragment> visibleFragment = getVisibleFragment();
        if (visibleFragment.isPresent() && visibleFragment.get() instanceof StopDetailsFragment) {
            ((StopDetailsFragment) visibleFragment.get()).finishMe(null);
        } else super.onBackPressed();
    }

    @NotNull
    private Optional<Fragment> getVisibleFragment() {
        List<Fragment> fragments = manager.getFragments();
        return fragments.stream().filter(Fragment::isVisible).findFirst();
    }

    private short getFragmentIdFromHashMap(Fragment fragment) {
        for (Map.Entry<Short, Fragment> entry : fragmentMap.entrySet()) {
            Short key = entry.getKey();
            Fragment frag = entry.getValue();
            if (frag == fragment) return key;
        }
        return -1;
    }

    @Override
    public void onRouteSelect(RoutesFromStopDetail routeDetail, StopDetail stopDetail) {
        if (routeDetail == null) return;
        RouteStopsFragment fragment = new RouteStopsFragment(routeDetail, stopDetail);
        fragmentMap.put(ROUTE_STOPS_FRAGMENT, fragment);
        navigateTo(ROUTE_STOPS_FRAGMENT);
    }
}