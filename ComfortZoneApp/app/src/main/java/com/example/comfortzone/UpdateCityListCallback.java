package com.example.comfortzone;

import com.example.comfortzone.models.WeatherData;

import java.util.List;

public interface UpdateCityListCallback {
    void onCityListUpdated(List<WeatherData> cityList);
}
