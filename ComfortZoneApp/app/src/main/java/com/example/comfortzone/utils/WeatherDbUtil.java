package com.example.comfortzone.utils;

import android.app.Activity;
import android.location.Location;

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.CityListCallback;
import com.example.comfortzone.LocationCallback;
import com.example.comfortzone.WeatherCallback;
import com.example.comfortzone.WeatherClient;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherData.Coordinates;
import com.example.comfortzone.models.WeatherGroupData;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeatherDbUtil {

    private static AllWeathersDatabase db;
    private static WeatherClient client;

    public static void maybeUpdateCitiesList(Activity context) {
        db = AllWeathersDatabase.getDbInstance(context.getApplicationContext());
        client = new WeatherClient();

        if (db.weatherDao().getAll().isEmpty()) {
            client.getCityData(new CityListCallback() {
                @Override
                public void onGetCityList(WeatherData[] weatherData) {
                    db.weatherDao().insertAll(weatherData);
                }
            });
        }

        LocationUtil.getLastLocation(context, new LocationCallback() {
            @Override
            public void onLocationUpdated(String lat, String lon) {
                Long timeNow = System.currentTimeMillis();
                Long hourAgo = timeNow - TimeUnit.HOURS.toMillis(1L);
                if (db.weatherDao().isUploadedOverHrAgo(hourAgo)) {
                    Coordinates coordinates = new WeatherData.Coordinates(lat, lon);
                    getAndStoreWeatherData(context, coordinates);
                }
            }
        });



    }

    private static void getAndStoreWeatherData(Activity context, Coordinates coordinates) {
        WeatherClient client = new WeatherClient();
        List<String> groupUrls = client.getGroupWeatherUrl(context);
        for (String apiUrl : groupUrls) {
            client.getGroupWeatherData(apiUrl, new WeatherCallback() {
                @Override
                public void onGetWeatherData(String data) {
                    Gson gson = new Gson();
                    WeatherGroupData weathers = gson.fromJson(data, WeatherGroupData.class);
                    saveCities(weathers, coordinates);
                }
            });
        }
    }

    public static void saveCities(WeatherGroupData weathers, Coordinates coordinates) {
        WeatherData[] weatherData = weathers.getWeathers();
        for (WeatherData weather : weatherData) {
            int id = weather.getId();
            WeatherData weatherFromDb = db.weatherDao().getWeatherById(id);
            weatherFromDb.setTempData(weather.getTempData());
            weatherFromDb.setTimeUploaded(weather.getTimeUploaded());
            float[] results = new float[1];
            Location.distanceBetween(coordinates.getLat(), coordinates.getLon(), weather.getCoord().getLat(), weather.getCoord().getLon(), results);
            weatherFromDb.setDistanceBetween(results[0]);
            db.weatherDao().updateWeatherData(weatherFromDb);
        }
    }

}
