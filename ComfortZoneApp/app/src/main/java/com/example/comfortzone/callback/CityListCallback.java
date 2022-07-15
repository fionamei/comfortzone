package com.example.comfortzone.callback;

import com.example.comfortzone.models.WeatherData;

public interface CityListCallback {
    void onGetCityList(WeatherData[] cityList);
}
