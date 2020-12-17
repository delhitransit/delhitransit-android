package com.delhitransit.delhitransit_android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.MapsFragmentDirections;
import com.delhitransit.delhitransit_android.fragment.route_stops.RouteStopsFragment;
import com.delhitransit.delhitransit_android.fragment.stop_details.StopDetailsFragment;
import com.delhitransit.delhitransit_android.interfaces.FragmentFinisherInterface;
import com.delhitransit.delhitransit_android.interfaces.OnStopMarkerClickedListener;
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

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class AppActivity extends AppCompatActivity implements OnStopMarkerClickedListener, FragmentFinisherInterface {

    public static final short MAPS_FRAGMENT = 0;
    private static final short STOP_DETAILS_FRAGMENT = 3;
    private static final short ROUTE_STOPS_FRAGMENT = 4;
    private final HashMap<Short, Fragment> fragmentMap = new HashMap<>();
    private short currentFragment = MAPS_FRAGMENT;
    private FragmentManager manager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        manager = getSupportFragmentManager();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
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
                        .setPositiveButton("Change IP address", (dialog, which) -> findViewById(R.id.settingsFragment).performClick()).show());
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
            case STOP_DETAILS_FRAGMENT: {
                if (!fragmentMap.containsKey(STOP_DETAILS_FRAGMENT)) {
                    transaction = null;
                } else {
                    Fragment fragment = showOrAddFragmentTransaction(STOP_DETAILS_FRAGMENT, managerFragments, transaction);
                    transaction.addToBackStack(fragment instanceof StopDetailsFragment ? StopDetailsFragment.KEY_FRAGMENT_BACKSTACK : null);
                }
                break;
            }
            case ROUTE_STOPS_FRAGMENT: {
                if (!fragmentMap.containsKey(ROUTE_STOPS_FRAGMENT)) {
                    transaction = null;
                } else {
                    Fragment fragment = showOrAddFragmentTransaction(ROUTE_STOPS_FRAGMENT, managerFragments, transaction);
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

    private Fragment showOrAddFragmentTransaction(short fragmentId, List<Fragment> managerFragments, FragmentTransaction transaction) {
        Fragment currentFragment = fragmentMap.getOrDefault(fragmentId, null);
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
        MapsFragmentDirections.ActionMapsFragmentToStopDetailsFragment action = MapsFragmentDirections.actionMapsFragmentToStopDetailsFragment(stop);
        navController.navigate(action);
    }

    @Override
    public void finishAndExecute(String backStackKey, @NotNull Runnable runOnFinish) {
        manager.popBackStackImmediate(backStackKey, POP_BACK_STACK_INCLUSIVE);
        getVisibleFragment().ifPresent(it -> navigateTo(getFragmentIdFromHashMap(it)));
        runOnFinish.run();
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

}