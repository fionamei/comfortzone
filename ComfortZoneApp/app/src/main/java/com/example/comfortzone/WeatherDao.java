package com.example.comfortzone;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.comfortzone.models.WeatherData;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM WeatherData")
    List<WeatherData> getAll();

    @Insert
    void insertAll(WeatherData... weatherData);

    @Query("DELETE FROM WeatherData")
    void deleteEntireTable();

    @Query("SELECT * FROM WeatherData WHERE timeUploaded < :hourAgo")
    List<WeatherData> getUploadTimes(long hourAgo);

    @Update
    void updateWeatherData(WeatherData weatherData);

    @Query("SELECT * FROM weatherdata WHERE id = :id LIMIT 1")
    WeatherData getWeatherById(int id);
}
