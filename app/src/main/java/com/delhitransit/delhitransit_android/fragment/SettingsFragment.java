package com.delhitransit.delhitransit_android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.delhitransit.delhitransit_android.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(
                getContext().getApplicationContext(),
                R.xml.root_preferences,
                false
        );
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key != null) {
            if (key.equals("github")) {
                openWebpage(getString(R.string.github_url));
                return true;
            } else if (key.equals("otd")) {
                openWebpage(getString(R.string.otd_url));
                return true;
            }else if (key.equals("guide")){
                openWebpage("https://cse.mait.ac.in/index.php/people/faculty?id=386");
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void openWebpage(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}