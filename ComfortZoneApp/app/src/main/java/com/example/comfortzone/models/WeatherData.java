package com.example.comfortzone.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class WeatherData {

    @SerializedName("name")
    private String city;

    @SerializedName("main")
    private TempData tempData;

    private String date;
    private String time;

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

    public void setDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(dtf);
        this.date = date;
    }

    public void setTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(dtf);
        this.time = time;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }
}
