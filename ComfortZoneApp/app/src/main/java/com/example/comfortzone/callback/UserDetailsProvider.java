package com.example.comfortzone.callback;

import com.example.comfortzone.models.WeatherData.Coordinates;

import java.util.HashSet;

public interface UserDetailsProvider {
    Coordinates getLocation();

    String getIataCode();

    HashSet<Integer> getSavedCities();

    Boolean[] getIsFahrenheit();
}
