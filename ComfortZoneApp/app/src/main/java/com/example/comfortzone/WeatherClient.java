package com.example.comfortzone;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.comfortzone.models.City;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherClient extends OkHttpClient {

    public static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    public static final String API_BASE_URL = String.format("https://api.openweathermap.org/data/2.5/weather?appid=%s", API_KEY);
    public static final String API_GROUP_BASE_URL = String.format("https://api.openweathermap.org/data/2.5/group?appid=%s", API_KEY);
    public static final String CITY_URL = "https://raw.githubusercontent.com/fionamei/comfortzone/main/city.json";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_UNIT = "units";
    public static final String TAG = "WeatherClient";
    public static final String FAHRENHEIT = "imperial";
    public static final String CELSIUS = "metric";
    public static final String KEY_ID = "id";


    private String getWeatherURL(String lat, String lon) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(KEY_LAT, lat);
        urlBuilder.addQueryParameter(KEY_LON, lon);
        urlBuilder.addQueryParameter(KEY_UNIT, FAHRENHEIT);

        return urlBuilder.build().toString();
    }

    public void getWeatherData(String lat, String lon, getWeatherCallback weatherCallback) {
        String url = getWeatherURL(lat, lon);
        final Request request = new Request.Builder()
                .url(url)
                .build();
        newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "failed making api request " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    weatherCallback.weatherData(data);
                } catch (IOException e) {
                    Log.e(TAG, "could not get body" + e);
                }
            }
        });}

    private String getGroupWeatherUrl(List<City> cityList) {
        List<String> ids = cityList.stream().map(city -> String.valueOf(city.getId())).collect(Collectors.toList());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_GROUP_BASE_URL).newBuilder();
        String idString = String.join(",", ids);
        urlBuilder.addQueryParameter(KEY_ID, idString);

        return urlBuilder.build().toString();
    }

    public void getCityData() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(CITY_URL).newBuilder();
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "failed getting city data " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Log.i(TAG, "response data is " + data);
            }
        });
    }
}
