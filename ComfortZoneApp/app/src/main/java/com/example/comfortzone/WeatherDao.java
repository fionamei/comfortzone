package com.example.comfortzone;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.comfortzone.models.WeatherData;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM WeatherData")
    List<WeatherData> getAll();

    // WHERE `temp` BETWEEN :max AND :min
//    @Query("SELECT * FROM WeatherData WHERE `temp` > :temp")
//    List<WeatherDao> findByTemp(int temp);

    @Insert
    void insertAll(WeatherData... weatherData);
}
