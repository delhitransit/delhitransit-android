package com.delhitransit.delhitransit_android;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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

//    override fun onPreferenceTreeClick(pref: Preference?): Boolean {
//
//        // Return false if you want pref to be updated
//
//        when (pref?.key) {
//            getString(R.string.server_ip_address_key) -> {
//                // Return true if you want pref to be updated
//                pref.setOnPreferenceChangeListener { preference, newValue ->
//                    if (preference is EditTextPreference) {
//                        Toast.makeText(
//                                context,
//                                "New ip ${newValue as String}",
//                                Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    // Update IP address if string is a valid ip address
//                    val matches = Patterns.IP_ADDRESS.matcher(newValue as String).matches()
//                    matches
//                }
//            }
//
//        }
//
//        Log.v(LOG_TAG, pref?.key ?: "Pref is null")
//
//
//        return false
//
//    }
//}

}