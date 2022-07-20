package com.example.comfortzone.callback;

import com.example.comfortzone.models.WeatherData.Coordinates;

import java.util.HashSet;

public interface UserDetailsCallback {
    Coordinates getLocation();

    String getIataCode();

    HashSet<Integer> getSavedCities();
}