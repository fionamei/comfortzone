package com.example.comfortzone.callback;

import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherData.Coordinates;

import java.util.HashSet;

import rx.Observable;

public interface UserDetailsProvider {
    Coordinates getLocation();

    String getIataCode();

    HashSet<Integer> getSavedCities();

    Boolean getIsFahrenheit();

    void setIsFahrenheit(boolean bool);

    WeatherData getCurrentWeatherData();

    Observable<Object> fetchNewWeatherData();
}
