package com.example.comfortzone.models;

import com.google.gson.annotations.SerializedName;

public class WeatherData {

    @SerializedName("name")
    private String city;

    @SerializedName("main")
    private TempData tempData;

    public String getCity() {
        return city;
    }

    public TempData getTempData() {
        return tempData;
    }

    public class TempData {
        @SerializedName("feels_like")
        private double temp;

        public double getTemp() {
            return temp;
        }
    }
}
