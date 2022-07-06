package com.example.comfortzone;

import com.example.comfortzone.models.WeatherData;

public interface CityListCallback {
    void onGetCityList(WeatherData[] cityList);
}
