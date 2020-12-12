package com.delhitransit.delhitransit_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppActivity extends AppCompatActivity {

    public static final short SETTINGS_FRAGMENT = 0;
    private static final String INTENT_FRAGMENT_KEY = "fragmentKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.settings_tab_button);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map_tab_button) {
                onBackPressed();
                return true;
            }
            return true;
        });

    }

    public static Intent navigateTo(Context source, short fragmentID) {
        Intent intent = new Intent(source, AppActivity.class);
        intent.putExtra(INTENT_FRAGMENT_KEY, fragmentID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }
}