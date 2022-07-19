package com.example.comfortzone.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.comfortzone.models.WeatherData;

@Database(entities = {WeatherData.class}, version = 6)
public abstract class AllWeathersDatabase extends RoomDatabase {

    public static final String DB_NAME = "All_Weathers_Database";

    public abstract WeatherDao weatherDao();

    private static AllWeathersDatabase INSTANCE;

    public static AllWeathersDatabase getDbInstance(Context context) {
        if (context == null || INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AllWeathersDatabase.class, DB_NAME)
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
