package com.delhitransit.delhitransit_android.helperclasses;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.delhitransit.delhitransit_android.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.card.MaterialCardView;

public class ViewMarker {

    public static final int YOUR_LOCATION = 11;
    public static final int BUS_STOP = 12;
    public static final int FAVOURITE = 13;
    public final View view;
    final int SIZE = 50;

    public ViewMarker(Context context, String s) {
        this(context, s, Color.GREEN, YOUR_LOCATION);
    }

    public ViewMarker(Context context, String s, String relation, int markerType) {
        this(context, s, Color.GREEN, relation, markerType);
    }

    public ViewMarker(Context context, String s, int color, int markerType) {
        this(context, s, color, "", markerType);
    }

    public ViewMarker(Context context, String s, int color, String relation, int markerType) {
        View view = LayoutInflater.from(context).inflate(R.layout.marker_heading_view, null, false);
        TextView headingTextView = view.findViewById(R.id.heading_text_view);
        ImageView iconImageView = view.findViewById(R.id.icon_image_view);
        if (relation.isEmpty()) {
            headingTextView.setText(getTruncatedStopNameString(s));
        } else {
            SpannableString temp = new SpannableString(relation + " \n" + getTruncatedStopNameString(s));
            temp.setSpan(new RelativeSizeSpan(1.2f), relation.length() + 2, temp.length(), 0);
            temp.setSpan(new StyleSpan(Typeface.BOLD), relation.length() + 2, temp.length(), 0);
            temp.setSpan(new StyleSpan(Typeface.ITALIC), relation.length() + 2, temp.length(), 0);
            headingTextView.setText(temp);
        }
        switch (markerType) {
            case BUS_STOP:
                iconImageView.setBackgroundResource(R.drawable.bus_stop_icon);
                break;
            case FAVOURITE:
                iconImageView.setBackgroundResource(R.drawable.ic_baseline_star_24);
                color = Color.rgb(249, 166, 2);
                break;
        }
        if (color != Color.GREEN) {
            MaterialCardView headingCardView = view.findViewById(R.id.heading_card_view);
            headingCardView.setCardBackgroundColor(color);
            headingCardView.setStrokeColor(darkenColor(color));
        }
        this.view = view;
    }

    public ViewMarker(Context context) {
        view = new View(context);
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_marker_icon_150);
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, SIZE, SIZE, true);
        view.setBackground(new BitmapDrawable(context.getResources(), bMapScaled));

    }

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    public static BitmapDescriptor vectorToBitmap(Resources resources, @DrawableRes int id, @ColorInt int color, float scale) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(resources, id, null);
        Bitmap bitmap = Bitmap.createBitmap(
                (int) (scale * vectorDrawable.getIntrinsicWidth()),
                (int) (scale * vectorDrawable.getIntrinsicHeight()),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static String getTruncatedStopNameString(String s) {
        if (s.length() < 15) {
            return s;
        } else {
            return s.substring(0, 15) + "...";
        }
    }

    public static Bitmap getBitmap(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);
        return bitmap;
    }

    @ColorInt
    int darkenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}
