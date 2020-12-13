package com.delhitransit.delhitransit_android.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

@Dao
public interface StopResponseDataDao {

    @Query("SELECT * FROM stop")
    List<StopsResponseData> getAllStops();

    @Insert
    void insertAll(StopsResponseData... stops);

}
