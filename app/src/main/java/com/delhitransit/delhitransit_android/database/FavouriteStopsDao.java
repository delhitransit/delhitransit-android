package com.delhitransit.delhitransit_android.database;

import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FavouriteStopsDao {

    @Query("SELECT * FROM favourite_stop")
    LiveData<List<StopDetail>> getAllFavouriteStops();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(StopDetail... stops);

    @Query("DELETE FROM favourite_stop")
    void deleteAll();

    @Query("DELETE FROM favourite_stop WHERE stopId==:stopId")
    void deleteByStopId(int stopId);

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_stop WHERE stopId=:stopId)")
    LiveData<Boolean> contains(int stopId);
}
