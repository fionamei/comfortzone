package com.example.comfortzone.models;

import com.google.gson.annotations.SerializedName;

public class WeatherGroupData {

    public static final String TAG = "flight";

    @SerializedName("list")
    private WeatherData[] weatherDataList;

    public WeatherData[] getWeathers () {
        return this.weatherDataList;
    }
}
