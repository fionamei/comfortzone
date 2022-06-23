package com.example.comfortzone;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherClient extends OkHttpClient {

    public static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    public static final String API_BASE_URL = String.format("https://api.openweathermap.org/data/2.5/weather?appid=%s", API_KEY);
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String TAG = "WeatherClient";


    public String getWeatherURL(String lat, String lon) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(KEY_LAT, lat);
        urlBuilder.addQueryParameter(KEY_LON, lon);
        String url = urlBuilder.build().toString();

        return url;
    }

    public String getWeatherData(String lat, String lon) {
        String url = getWeatherURL(lat, lon);
        final String[] data = new String[1];
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
                    data[0] = response.body().string();
                } catch (IOException e) {
                    Log.e(TAG, "could not get body" + e);
                }
            }
        });
        return data[0];
    }
}
