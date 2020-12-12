package com.delhitransit.delhitransit_android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppActivity extends AppCompatActivity {

    public static final short SETTINGS_FRAGMENT = 0;
    private static final String INTENT_FRAGMENT_KEY = "fragmentKey";

    private short currentFragment = SETTINGS_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        currentFragment = getIntent().getShortExtra(INTENT_FRAGMENT_KEY, SETTINGS_FRAGMENT);
        navigateTo(currentFragment);

        if (currentFragment == SETTINGS_FRAGMENT)
            bottomNav.setSelectedItemId(R.id.settings_tab_button);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map_tab_button) {
                onBackPressed();
                return true;
            }
            return true;
        });

    }

    private void navigateTo(short fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment frag;
        switch (fragment) {
            default:
            case SETTINGS_FRAGMENT: {
                frag = new SettingsFragment();
                currentFragment = SETTINGS_FRAGMENT;
                break;
            }
        }
        transaction.replace(R.id.fragment_container, frag);
        transaction.commit();
    }

    public static Intent navigateTo(Context source, short fragmentID) {
        Intent intent = new Intent(source, AppActivity.class);
        intent.putExtra(INTENT_FRAGMENT_KEY, fragmentID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }
}