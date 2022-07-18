package com.example.comfortzone.utils;

import com.example.comfortzone.models.ComfortLevelEntry;
import com.example.comfortzone.models.WeatherData;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserPreferenceUtil {

    public static final String TAG = "UserPreferenceUtil";
    public static final String KEY_SAVED_CITIES = "savedCities";

    public static void saveCity(ParseUser currentUser, int cityId) {
        currentUser.addUnique(KEY_SAVED_CITIES, cityId);
        currentUser.saveInBackground();
    }

    public static void deleteSavedCity(ParseUser currentUser, WeatherData cityToRemove) {
        List<Integer> toRemove = new ArrayList<>();
        toRemove.add(cityToRemove.getId());
        currentUser.removeAll(KEY_SAVED_CITIES, toRemove);
        currentUser.saveInBackground();
    }

}
