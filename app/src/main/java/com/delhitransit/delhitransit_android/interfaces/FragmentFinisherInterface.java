package com.delhitransit.delhitransit_android.interfaces;

import androidx.annotation.NonNull;

public interface FragmentFinisherInterface {
    void finishAndExecute(String backStackKey, @NonNull Runnable runOnFinish);
}
