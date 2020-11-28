package com.delhitransit.delhitransit_android.helperclasses;

import androidx.annotation.NonNull;

public class TimeConverter {
    public long hr, min, sec, hourOfDay;
    public String time, justHrAndMin, justHrAndMinWithState, state;

    public TimeConverter(Long time) {
        hourOfDay = (time / 3600);
        min = (time % 3600) / 60;
        sec = (time % 3600) % 60;
        if (hourOfDay > 12) {
            hr = hourOfDay % 12;
            state = "PM";
        } else {
            hr = hourOfDay;
            state = "AM";
        }
        this.time = hr + ":" + min + ":" + sec;
        if (min < 10) {
            justHrAndMinWithState = hr + ":0" + min + " " + state;
            justHrAndMin = hr + ":0" + min;
        } else {
            justHrAndMinWithState = hr + ":" + min + " " + state;
            justHrAndMin = hr + ":" + min;
        }

    }

    @NonNull
    @Override
    public String toString() {
        return time;
    }
}
