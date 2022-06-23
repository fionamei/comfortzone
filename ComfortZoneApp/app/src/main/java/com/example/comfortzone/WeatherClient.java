package com.example.comfortzone;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

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
}
