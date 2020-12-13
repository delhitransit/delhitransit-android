package com.delhitransit.delhitransit_android.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

@Dao
public interface FavouriteStopsDao {

    @Query("SELECT * FROM favourite_stop")
    LiveData<List<StopsResponseData>> getAllFavouriteStops();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(StopsResponseData... stops);

    @Query("DELETE FROM favourite_stop")
    void deleteAll();

    @Query("DELETE FROM favourite_stop WHERE stopId==:stopId")
    void deleteByStopId(int stopId);

}
