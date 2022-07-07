package com.example.comfortzone.utils;

import android.content.Context;

import com.example.comfortzone.AllWeathersDatabase;
import com.example.comfortzone.CityListCallback;
import com.example.comfortzone.WeatherCallback;
import com.example.comfortzone.GroupUrlCallback;
import com.example.comfortzone.WeatherClient;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherGroupData;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeatherDbUtil {

    private static AllWeathersDatabase db;
    private static WeatherClient client;

    public static void maybeUpdateCitiesList(Context context) {
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
        Long timeNow = System.currentTimeMillis();
        Long hourAgo = timeNow - TimeUnit.HOURS.toMillis(1L);
        if (db.weatherDao().isUploadedOverHrAgo(hourAgo)) {
            getAndStoreWeatherData(context);
        }
    }

    private static void getAndStoreWeatherData(Context context) {
        WeatherClient client = new WeatherClient();
        client.getGroupWeatherUrl(context, new GroupUrlCallback() {
            @Override
            public void onGetWeatherUrlGroupIds(List<String> groupUrls) {
                for (String apiUrl : groupUrls) {
                    client.getGroupWeatherData(apiUrl, new WeatherCallback() {
                        @Override
                        public void onGetWeatherData(String data) {
                            Gson gson = new Gson();
                            WeatherGroupData weathers = gson.fromJson(data, WeatherGroupData.class);
                            saveCities(weathers);
                        }
                    });
                }
            }
        });
    }

    public static void saveCities(WeatherGroupData weathers) {
        WeatherData[] weatherData = weathers.getWeathers();
        for (WeatherData weather : weatherData) {
            int id = weather.getId();
            WeatherData weatherFromDb = db.weatherDao().getWeatherById(id);
            weatherFromDb.setTempData(weather.getTempData());
            weatherFromDb.setTimeUploaded(weather.getTimeUploaded());
            db.weatherDao().updateWeatherData(weatherFromDb);
        }
    }
}
