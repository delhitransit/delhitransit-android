package com.delhitransit.delhitransit_android;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.delhitransit.delhitransit_android.database.AppDatabase;
import com.delhitransit.delhitransit_android.database.FavouriteStopsDao;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class StopResponseDaoTest {

    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeAndReadStopFromDb() {
        StopsResponseData stop = new StopsResponseData();
        stop.setLatitude(23.124556);
        stop.setLongitude(37.544556);
        stop.setName("My Testing Stop");
        stop.setStopId(3546);
        FavouriteStopsDao userDao = db.dao();
        userDao.insertAll(stop);
        List<StopsResponseData> stops = userDao.getAllFavouriteStops();
        assertThat(stops.size(), equalTo(1));
        StopsResponseData data = stops.get(0);
        assertThat(data.getLatitude(), equalTo(stop.getLatitude()));
        assertThat(data.getLongitude(), equalTo(stop.getLongitude()));
        assertThat(data.getName(), equalTo(stop.getName()));
        assertThat(data.getStopId(), equalTo(stop.getStopId()));
        Log.v(this.toString(), "Passed test : writeAndReadStopFromDb");
    }
}
