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
    
    public Object getTempData() {
        return new TempData();
    }

    private class TempData {
        @SerializedName("feels_like")
        private double temp;

        public double getTemp() {
            return temp;
        }

    }
}
