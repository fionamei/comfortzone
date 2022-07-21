package com.example.comfortzone.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Entity
@Parcel(analyze = WeatherData.class)
public class WeatherData {

    @PrimaryKey
    private int id;

    @ColumnInfo
    private int rank;

    @ColumnInfo
    @SerializedName("name")
    private String city;

    @ColumnInfo
    private String state;

    @Embedded
    @SerializedName("main")
    private TempData tempData;

    @Ignore
    private String date;
    @Ignore
    private String time;

    @ColumnInfo(name = "timeUploaded")
    @SerializedName("dt")
    private long timeUploaded;

    @Embedded
    private Coordinates coord;

    @ColumnInfo
    @SerializedName("image url")
    private String image;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private String iata;

    public float getDistanceBetween() {
        return distanceBetween;
    }

    public void setDistanceBetween(float distanceBetween) {
        this.distanceBetween = distanceBetween;
    }

    @ColumnInfo
    private float distanceBetween;

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    @Parcel
    public static class Coordinates {
        private double lat;
        private double lon;

        public Coordinates() {
        }

        public Coordinates(String lat, String lon) {
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);
        }

        public double getLat() {
            return this.lat;
        }

        public double getLon() {
            return this.lon;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

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

    @Ignore
    public void setDate(String date) {
        this.date = date;
    }

    @Ignore
    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(long timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    @Parcel
    public static class TempData {
        @SerializedName("feels_like")
        private double temp;

        public TempData() {}

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
