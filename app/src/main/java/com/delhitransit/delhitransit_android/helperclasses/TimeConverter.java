package com.delhitransit.delhitransit_android.helperclasses;

import android.icu.util.Calendar;

import androidx.annotation.NonNull;

import java.util.Date;

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

    public static long getSecondsSince12AM() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        return now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND);

    }

    public static long getTimeInSeconds(String time) {
        return Long.parseLong(time.substring(0, 2)) * 3600 + Long.parseLong(time.substring(3, 5)) * 60 + Long.parseLong(time.substring(6, 8));
    }

    @NonNull
    @Override
    public String toString() {
        return time;
    }
}
