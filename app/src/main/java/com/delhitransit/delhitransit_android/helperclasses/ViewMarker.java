package com.delhitransit.delhitransit_android.helperclasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.delhitransit.delhitransit_android.R;

public class ViewMarker {
    private final View view;
    Context context;

    public ViewMarker(Context context, String s) {
        this(context, s, Color.GREEN);
    }

    public ViewMarker(Context context, String s, int color) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.marker_heading_view, null, false);
        if (s.length() < 15)
            ((TextView) view.findViewById(R.id.heading_text_view)).setText(s);
        else {
            ((TextView) view.findViewById(R.id.heading_text_view)).setText(s.substring(0, 15) + "...");
        }
        if (color != Color.GREEN)
            view.findViewById(R.id.heading_linear_layout).setBackgroundColor(color);
    }

    public Bitmap getBitmap() {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.TRANSPARENT);

        view.draw(canvas);
        return bitmap;

    }
}
