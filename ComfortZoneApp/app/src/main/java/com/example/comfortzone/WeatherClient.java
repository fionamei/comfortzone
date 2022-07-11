package com.example.comfortzone;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherData.Coordinates;
import com.example.comfortzone.models.WeatherGroupData;
import com.example.comfortzone.utils.WeatherDbUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

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
    public static final int MAX_API_CITIES = 20;


    private String getWeatherURL(String lat, String lon) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(KEY_LAT, lat);
        urlBuilder.addQueryParameter(KEY_LON, lon);
        urlBuilder.addQueryParameter(KEY_UNIT, FAHRENHEIT);

        return urlBuilder.build().toString();
    }

    public void getWeatherData(String lat, String lon, WeatherCallback weatherCallback) {
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
                    weatherCallback.onGetWeatherData(data);
                } catch (IOException e) {
                    Log.e(TAG, "could not get body" + e);
                }
            }
        });}

    public Observable<Object> getGroupWeatherData(String url, Coordinates coordinates) {
        Request request = new Request.Builder().url(url).build();
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "failed making group api request " + e);
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String data = response.body().string();
                        Gson gson = new Gson();
                        WeatherGroupData weathers = gson.fromJson(data, WeatherGroupData.class);
                        WeatherDbUtil.saveCities(weathers, coordinates);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public List<String> getGroupWeatherUrl(Context context) {
        AllWeathersDatabase db = AllWeathersDatabase.getDbInstance(context.getApplicationContext());
        List<WeatherData> cityList = db.weatherDao().getAll();
        List<String> ids = cityList.stream().map(city -> String.valueOf(city.getId())).collect(Collectors.toList());
        List<List<String>> batchesCities = Lists.partition(ids, MAX_API_CITIES);
        List<String> batchCityUrls = batchesCities.stream().map(batch -> getBatchUrl(batch)).collect(Collectors.toList());
        return batchCityUrls;
    }

    private String getBatchUrl(List<String> batchCity) {
        String idString = String.join(",", batchCity);
        return String.format(API_GROUP_BASE_URL + "&id=%s&units=%s", idString, FAHRENHEIT);
    }

    public void getCityData(CityListCallback callback) {
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
                Gson gson = new Gson();
                Type weatherListType = new TypeToken<WeatherData[]>(){}.getType();
                WeatherData[] weatherData = gson.fromJson(data, weatherListType);
                callback.onGetCityList(weatherData);
            }
        });
    }
}
