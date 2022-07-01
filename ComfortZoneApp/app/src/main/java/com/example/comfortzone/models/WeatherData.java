package com.example.comfortzone.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Entity
public class WeatherData {

    @PrimaryKey
    private int id;

    @ColumnInfo
    @SerializedName("name")
    private String city;

    @Embedded
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

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTempData(TempData tempData) {
        this.tempData = tempData;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static class TempData {
        @SerializedName("feels_like")
        private double temp;

        public TempData(double temp) {
            setTemp(temp);
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
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

    public int getId() {
        return this.id;
    }

}
