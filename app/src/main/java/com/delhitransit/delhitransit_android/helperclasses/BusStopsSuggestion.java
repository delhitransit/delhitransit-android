package com.delhitransit.delhitransit_android.helperclasses;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;


public class BusStopsSuggestion implements SearchSuggestion {

    private final String mBusStopName;
    private StopDetail stopDetail;
    private boolean mIsHistory = false;


    public BusStopsSuggestion(Parcel source) {
        this.mBusStopName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public BusStopsSuggestion(StopDetail stopDetail) {
        this.stopDetail = stopDetail;
        this.mBusStopName = stopDetail.getName();
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public StopDetail getStopDetail() {
        return stopDetail;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mBusStopName;
    }

    public static final Creator<BusStopsSuggestion> CREATOR = new Creator<BusStopsSuggestion>() {
        @Override
        public BusStopsSuggestion createFromParcel(Parcel in) {
            return new BusStopsSuggestion(in);
        }

        @Override
        public BusStopsSuggestion[] newArray(int size) {
            return new BusStopsSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBusStopName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
