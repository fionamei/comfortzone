package com.example.comfortzone.models;

import com.google.gson.annotations.SerializedName;

public class WeatherData {


    @SerializedName("name")
    private String city;
    @SerializedName("feels_like")
    private int temp;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
