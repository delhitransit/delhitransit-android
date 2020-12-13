package com.delhitransit.delhitransit_android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.MapsFragment;
import com.delhitransit.delhitransit_android.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class AppActivity extends AppCompatActivity {

    public static final short MAPS_FRAGMENT = 0;
    public static final short SETTINGS_FRAGMENT = 1;
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
            case SETTINGS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.settings_tab_button);
                break;
            case MAPS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.map_tab_button);
                break;
        }

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.map_tab_button) {
                navigateTo(MAPS_FRAGMENT);
            } else if (itemId == R.id.settings_tab_button) {
                navigateTo(SETTINGS_FRAGMENT);
            }
            return true;
        });

    }

    private void navigateTo(short fragmentId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentId) {
            default:
            case MAPS_FRAGMENT: {
                fragmentMap.put(MAPS_FRAGMENT, fragmentMap.getOrDefault(MAPS_FRAGMENT, new MapsFragment()));
                currentFragment = MAPS_FRAGMENT;
            }
            case SETTINGS_FRAGMENT: {
                fragmentMap.put(SETTINGS_FRAGMENT, fragmentMap.getOrDefault(SETTINGS_FRAGMENT, new SettingsFragment()));
                currentFragment = SETTINGS_FRAGMENT;
            }
        }
        Fragment fragment = fragmentMap.getOrDefault(fragmentId, fragmentMap.get(MAPS_FRAGMENT));
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}