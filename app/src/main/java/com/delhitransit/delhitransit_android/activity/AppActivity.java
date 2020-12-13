package com.delhitransit.delhitransit_android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.FavouriteStopsFragment;
import com.delhitransit.delhitransit_android.fragment.MapsFragment;
import com.delhitransit.delhitransit_android.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    public static final short MAPS_FRAGMENT = 0;
    public static final short SETTINGS_FRAGMENT = 1;
    public static final short FAVOURITE_STOPS_FRAGMENT=2;
    private static final String INTENT_FRAGMENT_KEY = "fragmentKey";
    private final HashMap<Short, Fragment> fragmentMap = new HashMap<>();
    private short currentFragment = MAPS_FRAGMENT;

    public static Intent navigateTo(Context source, short fragmentID) {
        Intent intent = new Intent(source, AppActivity.class);
        intent.putExtra(INTENT_FRAGMENT_KEY, fragmentID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        currentFragment = getIntent().getShortExtra(INTENT_FRAGMENT_KEY, MAPS_FRAGMENT);
        navigateTo(currentFragment);

        switch (currentFragment) {
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

    private void navigateTo(short fragmentId) {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> managerFragments = manager.getFragments();
        FragmentTransaction transaction = manager.beginTransaction();

        short previousFragment = currentFragment;

        Fragment prevFragment = fragmentMap.get(previousFragment);

        if (prevFragment != null && managerFragments.contains(prevFragment)) {
            prevFragment.onPause();
            transaction.hide(prevFragment);
        }

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
        }
        transaction.commit();
    }

    private void showOrAddFragmentTransaction(short fragmentId, Fragment defaultFragment, List<Fragment> managerFragments, FragmentTransaction transaction) {
        Fragment currentFragment = fragmentMap.getOrDefault(fragmentId, defaultFragment);
        fragmentMap.put(fragmentId, currentFragment);
        this.currentFragment = fragmentId;
        if (!managerFragments.contains(currentFragment)) {
            transaction.add(R.id.fragment_container, currentFragment);
        } else {
            transaction.show(currentFragment);
            currentFragment.onResume();
        }
    }

}