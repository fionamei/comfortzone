package com.example.comfortzone.callback;

import com.example.comfortzone.models.WeatherData.Coordinates;

public interface UserLocationCallback {
    Coordinates getLocation();

    String getIataCode();
}
