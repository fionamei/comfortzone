package com.example.comfortzone.utils;

import android.app.Activity;
import android.location.Location;

import com.example.comfortzone.data.local.AllWeathersDatabase;
import com.example.comfortzone.callback.CityListCallback;
import com.example.comfortzone.input.callbacks.LocationCallback;
import com.example.comfortzone.data.network.WeatherClient;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.models.WeatherData.Coordinates;
import com.example.comfortzone.models.WeatherGroupData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class WeatherDbUtil {

    private static AllWeathersDatabase db;
    private static WeatherClient client;

    public static Observable<Object> maybeUpdateCitiesList(Activity context) {
        db = AllWeathersDatabase.getDbInstance(context.getApplicationContext());
        client = new WeatherClient();

        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if (db.weatherDao().getAll().isEmpty()) {
                    client.getCityData(new CityListCallback() {
                        @Override
                        public void onGetCityList(WeatherData[] weatherData) {
                            db.weatherDao().insertAll(weatherData);
                            saveNewTemps(context).subscribe(subscriber);
                        }
                    });
                } else {
                    saveNewTemps(context).subscribe(subscriber);
                }
            }
        });

    }

    private static Observable<Object> saveNewTemps(Activity activity) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                LocationUtil.getLastLocation(activity, new LocationCallback() {
                    @Override
                    public void onLocationUpdated(String lat, String lon) {
                        Long timeNow = System.currentTimeMillis();
                        Long hourAgo = timeNow - TimeUnit.HOURS.toMillis(1L);
                        if (db.weatherDao().isUploadedOverHrAgo(hourAgo)) {
                            Coordinates coordinates = new WeatherData.Coordinates(lat, lon);
                            Observable<Object> ob = getAndStoreWeatherData(activity, coordinates); // returns one observable
                            ob.subscribe(subscriber);
                        } else {
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        });
    }

    private static Observable<Object> getAndStoreWeatherData(Activity context, Coordinates coordinates) {
        WeatherClient client = new WeatherClient();
        List<String> groupUrls = client.getGroupWeatherUrl(context);
        List<Observable<Object>> listObservables = new ArrayList<>();
        for (String apiUrl : groupUrls) {
            listObservables.add(client.getGroupWeatherData(apiUrl, coordinates));
        }
        return Observable.merge(listObservables);
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
