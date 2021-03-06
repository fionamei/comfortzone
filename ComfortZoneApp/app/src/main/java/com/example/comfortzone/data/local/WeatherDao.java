package com.example.comfortzone.data.local;

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

    @Query("SELECT EXISTS (SELECT * FROM WeatherData WHERE timeUploaded < :hourAgo)")
    boolean isUploadedOverHrAgo(long hourAgo);

    @Update
    void updateWeatherData(WeatherData weatherData);

    @Query("SELECT * FROM weatherdata WHERE id = :id LIMIT 1")
    WeatherData getWeatherById(int id);

    @Query("SELECT * FROM WeatherData WHERE `temp` BETWEEN :lowRange AND :highRange")
    List<WeatherData> getRange(int lowRange, int highRange);
}
