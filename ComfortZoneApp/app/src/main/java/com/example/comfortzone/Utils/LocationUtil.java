package com.example.comfortzone.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LocationUtil {

    public static final String TAG = "locationutil";
    public static final int PERMISSION_ID = 44;
    public static String lat;
    public static String lon;
    public static long ONE_HOUR_MILLI = TimeUnit.HOURS.toMillis(1L);


    @SuppressLint("MissingPermission")
    public static void getLastLocation(Activity activity, FusedLocationProviderClient fusedLocationClient) {
        if (checkPermissions(activity)) {
            if (isLocationEnabled(activity)) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData(activity, fusedLocationClient);
                        } else {
                            long locationTime = location.getTime();
                            long currentTime = System.currentTimeMillis();
                            long diff = currentTime - locationTime;
                            if (diff > ONE_HOUR_MILLI) {
                                requestNewLocationData(activity, fusedLocationClient);
                            } else {
                                lat = String.valueOf(location.getLatitude());
                                lon = String.valueOf(location.getLongitude());
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(activity, "please turn on your location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        } else {
            requestPermissions(activity);
        }
    }

    private static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ID);
    }

    private static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private static void requestNewLocationData(Context context, FusedLocationProviderClient fusedLocationClient) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private static LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        }
    };

}
