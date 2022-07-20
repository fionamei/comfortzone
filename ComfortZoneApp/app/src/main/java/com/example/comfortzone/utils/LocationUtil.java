package com.example.comfortzone.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.comfortzone.flight.callbacks.IataCallback;
import com.example.comfortzone.flight.data.IataClient;
import com.example.comfortzone.input.callbacks.LocationCallback;
import com.example.comfortzone.models.WeatherData;
import com.example.comfortzone.ui.HostActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class LocationUtil {

    public static final String TAG = "locationutil";
    public static final long ONE_HOUR_MILLI = TimeUnit.HOURS.toMillis(1L);
    private static FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    public static void getLastLocation(Activity activity, LocationCallback locationCallback) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (isLocationEnabled(activity)) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location == null || System.currentTimeMillis() - location.getTime() > ONE_HOUR_MILLI) {
                        requestNewLocationData(activity, locationCallback);
                    } else {
                        String lat = String.valueOf(location.getLatitude());
                        String lon = String.valueOf(location.getLongitude());
                        locationCallback.onLocationUpdated(lat, lon);
                    }
                }
            });
        } else {
            Toast.makeText(activity, "please turn on your location", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }
    }

    private static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private static void requestNewLocationData(Context context, LocationCallback locationCallback) {
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            Toast.makeText(context, "unable to get location, please try again later", Toast.LENGTH_LONG).show();
                        } else {
                            String lat = String.valueOf(location.getLatitude());
                            String lon = String.valueOf(location.getLongitude());
                            locationCallback.onLocationUpdated(lat, lon);
                        }
                    }
                });
    }

    public static Observable<Object> getLocationObservable(Activity activity) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                LocationUtil.getLastLocation(activity, new LocationCallback() {
                    @Override
                    public void onLocationUpdated(String lat, String lon) {
                        getIataObservable(lat, lon).subscribe(subscriber);
                    }
                });
            }
        });
    }

    private static Observable<Object> getIataObservable(String lat, String lon) {
        IataClient iataClient = new IataClient();
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                iataClient.getIataResponse(lat, lon, new IataCallback() {
                    @Override
                    public void onGetIata(String iata) {
                        Pair<WeatherData.Coordinates, String> mergedInfo = new Pair<>(new WeatherData.Coordinates(lat, lon), iata);
                        subscriber.onNext(mergedInfo);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }
}
