package com.delhitransit.delhitransit_android.helperclasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.delhitransit.delhitransit_android.R;

import static com.delhitransit.delhitransit_android.helperclasses.ViewMarker.getTruncatedStopNameString;

public class TrackerStopMarker {

    public final View view;

    public TrackerStopMarker(Context context, String s) {
        View view = LayoutInflater.from(context).inflate(R.layout.marker_stop_tracker, null, false);
        TextView headingTextView = view.findViewById(R.id.marker_stop_tracker_heading_text_view);
        headingTextView.setText(getTruncatedStopNameString(s));
        this.view = view.findViewById(R.id.marker_stop_tracker_heading_card_view);
    }

    public Bitmap getBitmap() {
        return ViewMarker.getBitmap(this.view);
    }


}
